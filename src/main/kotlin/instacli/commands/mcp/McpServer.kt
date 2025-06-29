package instacli.commands.mcp

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import instacli.files.CliFile
import instacli.language.*
import instacli.util.*
import io.ktor.utils.io.streams.*
import io.modelcontextprotocol.kotlin.sdk.*
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.buffered
import kotlin.concurrent.thread
import kotlin.io.path.name

object McpServer : CommandHandler("Mcp server", "ai/mcp"), ObjectHandler, DelayedResolver {

    private val servers = mutableMapOf<String, Server>()

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val info = data.toDomainObject(McpServerInfo::class)

        // Stop server
        if (info.stop) {
            this.stopServer(info.name)
            return null
        }

        // TODO Resolve top level properties but not the scripts

        val server = servers.getOrPut(info.name) {
            println("Starting Instacli MCP Server for ${context.cliFile.name} with name ${info.name}")
            Server(
                Implementation(
                    name = info.name,
                    version = info.version
                ),
                ServerOptions(
                    capabilities = ServerCapabilities(
                        tools = ServerCapabilities.Tools(listChanged = true),
                        resources = ServerCapabilities.Resources(subscribe = false, listChanged = true),
                        prompts = ServerCapabilities.Prompts(listChanged = true)
                    )
                )
            )
        }

        info.tools.forEach { (toolName, tool) ->
            server.addTool(toolName, tool, context.clone())
        }

        info.resources.forEach { (resourceURI, resource) ->
            server.addResource(resourceURI, resource, context.clone())
        }

        info.prompts.forEach { (promptName, prompt) ->
            server.addPrompt(promptName, prompt, context.clone())
        }

        // Listen to standard IO
        startServer(info.name, server)

        return null
    }

    private fun startServer(name: String, server: Server) {
        val transport = StdioServerTransport(
            System.`in`.asInput(),
            System.out.asSink().buffered()
        )

        thread(start = true, isDaemon = false, name = "MCP Server - $name") {
            println("[${Thread.currentThread().name}] Starting server ")
            runBlocking {
                server.connect(transport)

                val done = Job()
                server.onClose {
                    done.complete()
                }
                if (servers.contains(name)) {
                    done.join()
                    println("[${Thread.currentThread().name}] Stopping server ")
                } else {
                    println("[${Thread.currentThread().name}] Server stopped before it could start")
                }
            }
        }
    }

    fun stopServer(name: String) {
        val server = servers.remove(name) ?: return
        runBlocking {
            server.close()
        }
    }

    private fun Server.addTool(toolName: String, tool: ToolInfo, localContext: ScriptContext) {

        // TODO add support for required fields
        addTool(
            toolName,
            tool.description,
            Tool.Input(
                properties = tool.inputSchema.toKotlinx()
            ),
        ) { request ->
            // Set up context for the tool execution
            localContext.variables[INPUT_VARIABLE] = request.arguments.toJackson()

            // Run script
            val result: JsonNode? = if (tool.script is TextNode) {
                // Local script file
                val file = localContext.scriptDir.resolve(tool.script.textValue())
                CliFile(file).run(localContext)
            } else {
                // Inline script
                tool.script.run(localContext)
            }

            // Process result
            // TODO handle lists
            // TODO handle errors
            val output = result.toDisplayYaml()
            CallToolResult(content = listOf(TextContent(output)))
        }
    }

    private fun Server.addResource(resourceURI: String, resource: ResourceInfo, localContext: ScriptContext) {
        addResource(
            uri = resourceURI,
            name = resource.name,
            description = resource.description,
            mimeType = resource.mimeType
        ) { request ->

            val result: JsonNode? = if (resource.script is TextNode) {
                // Local script file
                val file = localContext.scriptDir.resolve(resource.script.textValue())
                CliFile(file).run(localContext)
            } else {
                // Inline script
                resource.script.run(localContext)
            }

            ReadResourceResult(
                contents = listOf(
                    TextResourceContents(result.toDisplayYaml(), request.uri, resource.mimeType)
                )
            )
        }
    }

    private fun Server.addPrompt(promptName: String, prompt: PromptInfo, localContext: ScriptContext) {
        addPrompt(
            name = promptName,
            description = prompt.description,
            arguments = prompt.arguments.map { argument ->
                PromptArgument(
                    name = argument.name,
                    description = argument.description,
                    required = argument.required
                )
            }
        ) { request ->
            // Set up context for the prompt execution
            localContext.variables[INPUT_VARIABLE] = Json.newObject(request.arguments ?: emptyMap())

            // Run script
            val result: JsonNode? = if (prompt.script is TextNode) {
                // Local script file
                val file = localContext.scriptDir.resolve(prompt.script.textValue())
                CliFile(file).run(localContext)
            } else {
                // Inline script
                prompt.script.run(localContext)
            }

            // Process result
            GetPromptResult(
                "Description for ${request.name}",
                messages = listOf(
                    PromptMessage(
                        role = Role.user,
                        content = TextContent(result.toDisplayYaml())
                    )
                )
            )
        }
    }
}

data class McpServerInfo(
    val name: String,
    val version: String,
    val stop: Boolean = false,

    @JsonAnySetter
    val tools: MutableMap<String, ToolInfo> = mutableMapOf(),

    @JsonAnySetter
    val resources: MutableMap<String, ResourceInfo> = mutableMapOf(),

    @JsonAnySetter
    val prompts: MutableMap<String, PromptInfo> = mutableMapOf()
)

data class ToolInfo(
    val description: String,
    val inputSchema: ObjectNode,
    val script: JsonNode
)

data class ResourceInfo(
    val name: String,
    val description: String,
    val script: JsonNode,
    val mimeType: String = "text/plain"
)

data class PromptInfo(
    val name: String,
    val description: String,
    val arguments: List<PromptArgumentInfo>,
    val script: JsonNode
)

data class PromptArgumentInfo(
    val name: String,
    val description: String,
    val required: Boolean = true,
)

fun main() {
    println("Hello from main thread: ${Thread.currentThread().name}")

    thread(start = true, isDaemon = false) {
        println("Hello from daemon thread: ${Thread.currentThread().name}")
        Thread.sleep(3000)
        println("Waited 3 seconds in daemon thread: ${Thread.currentThread().name}")
    }

}