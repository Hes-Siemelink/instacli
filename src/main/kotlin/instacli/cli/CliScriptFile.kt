package instacli.cli

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.engine.*
import instacli.util.Yaml
import java.io.File

class CliScriptFile(val scriptFile: File) : CommandInfo, CommandHandler(asScriptCommand(scriptFile.name)) {

    override val name: String = asCliCommand(scriptFile.name)
    override val description: String by lazy { cliScript.description ?: asScriptCommand(name) }

    val cliScript by lazy { CliScript.from(scriptNodes) }
    private val scriptNodes: List<JsonNode> by lazy { Yaml.parse(scriptFile) }

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        return run(VariableScope(data, context))
    }

    fun run(context: ScriptContext = ScriptDirectoryContext(scriptFile)): JsonNode? {
        try {
            return cliScript.run(context)
        } catch (a: Break) {
            println(a.output.textValue())
            return a.output
        }
    }
}

class ExecuteCliScriptFile : CommandHandler("Run script"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val fileName = data["file"] ?: throw CliScriptException("Run script needs 'file' field.")
        val scriptFile = File(context.scriptLocation.parent, fileName.asText())

        return CliScriptFile(scriptFile).execute(data, context)
    }
}