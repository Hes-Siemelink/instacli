package instacli.script.files

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.script.commands.ScriptInfoHandler
import instacli.script.execution.*
import instacli.util.Yaml
import java.io.File

class CliScriptFile(private val scriptFile: File) : CommandInfo, CommandHandler(asScriptCommand(scriptFile.name)) {

    override val name: String = asCliCommand(scriptFile.name)
    override val description: String by lazy { findDescription() }

    private val scriptNodes: List<JsonNode> by lazy { Yaml.parse(scriptFile) }

    private fun findDescription(): String {
        for (node in scriptNodes) {
            if (node.has(ScriptInfoHandler().name)) {
                val value = node.get(ScriptInfoHandler().name)?.get("description")?.textValue()
                if (value != null) {
                    return value
                }
            }
        }
        return asScriptCommand(name)
    }

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        return run(VariableScope(data, context))
    }

    fun run(context: ScriptContext = ScriptDirectoryContext(scriptFile)): JsonNode? {
        return CliScript.from(scriptNodes).run(context)
    }
}

class ExecuteCliScriptFile : CommandHandler("Run script"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val fileName = data["file"] ?: throw CliScriptException("Run script needs 'file' field.")
        val scriptFile = File(context.scriptLocation.parent, fileName.asText())

        return CliScriptFile(scriptFile).execute(data, context)
    }
}