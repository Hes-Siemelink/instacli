package instacli.commands.mcp

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.*
import instacli.util.toDisplayYaml
import instacli.util.toDomainObject
import instacli.util.toJackson
import instacli.util.toKotlinx
import io.ktor.utils.io.streams.*
import io.modelcontextprotocol.kotlin.sdk.*
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.buffered

object McpServer : CommandHandler("Mcp server", "ai/mcp"), ObjectHandler, DelayedResolver {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val info = data.toDomainObject(McpServerInfo::class)

        // TODO Resolve top level properties but not the scripts

        val server = Server(
            Implementation(
                name = info.name,
                version = info.version
            ),
            ServerOptions(
                capabilities = ServerCapabilities(tools = ServerCapabilities.Tools(listChanged = true))
            )
        )

        info.tools.forEach { (toolName, tool) ->
            server.addTool(toolName, tool, context.clone())
        }

        // Listen to standard IO
        startBlockingStdioServer(server)

        return null
    }

    private fun startBlockingStdioServer(server: Server) {
        val transport = StdioServerTransport(
            System.`in`.asInput(),
            System.out.asSink().buffered()
        )

        runBlocking {
            server.connect(transport)
            val done = Job()
            server.onClose {
                done.complete()
            }
            done.join()
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
            localContext.variables[INPUT_VARIABLE] = request.arguments.toJackson()
            val result = tool.script.run(localContext)
            val output = result.toDisplayYaml()
            CallToolResult(content = listOf(TextContent(output)))
        }
    }
}

data class McpServerInfo(
    val name: String,
    val version: String,

    @JsonAnySetter
    val tools: MutableMap<String, ToolInfo> = mutableMapOf()
)

data class ToolInfo(
    val description: String,
    val inputSchema: ObjectNode,
    val script: ObjectNode
)

