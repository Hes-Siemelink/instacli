package instacli.cli

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.engine.*
import instacli.util.Yaml
import java.io.File

class CliFile(val cliFile: File) : CommandInfo, CommandHandler(asScriptCommand(cliFile.name)) {

    override val name: String = asCliCommand(cliFile.name)
    override val description: String by lazy { script.description ?: asScriptCommand(name) }

    val script by lazy { Script.from(scriptNodes) }
    private val scriptNodes: List<JsonNode> by lazy { Yaml.parse(cliFile) }

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        val localContext = CliFileContext(cliFile, context, variables = Yaml.mutableMapOf(data))
        return run(localContext)
    }

    fun run(context: ScriptContext = CliFileContext(cliFile)): JsonNode? {
        return try {
            script.run(context)
        } catch (a: Break) {
            a.output
        }
    }
}

class RunScript : CommandHandler("Run script"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val fileName = data["file"] ?: throw CommandFormatException("Run script needs 'file' field.", data)
        val cliFile = File(context.cliFile.parent, fileName.asText())

        return CliFile(cliFile).execute(data, context)
    }
}