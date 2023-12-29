package instacli.cli

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.script.*
import instacli.util.Yaml
import java.io.File

class CliFile(val cliFile: File) : CommandInfo, CommandHandler(asScriptCommand(cliFile.name)), AnyHandler {

    override val name: String = asCliCommand(cliFile.name)
    override val description: String by lazy { script.description ?: asScriptCommand(name) }

    val script by lazy { Script.from(scriptNodes) }
    private val scriptNodes: List<JsonNode> by lazy { Yaml.parse(cliFile) }

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        val localContext = CliFileContext(cliFile, context, variables = Yaml.mutableMapOf(data))
        return runFile(localContext)
    }

    fun runFile(context: CliFileContext = CliFileContext(cliFile)): JsonNode? {
        return try {
            script.runScript(context)
        } catch (a: Break) {
            a.output
        } catch (e: InstacliException) {
            e.context = context.cliFile.name
            throw e
        }
    }
}

class RunScript : CommandHandler("Run script"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val fileName = data["file"] ?: throw CommandFormatException("Run script needs 'file' field.")
        val cliFile = File(context.getScriptDir(), fileName.asText())

        return handleCommand(CliFile(cliFile), data, context)
    }
}