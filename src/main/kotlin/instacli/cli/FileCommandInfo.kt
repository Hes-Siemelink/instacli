package instacli.cli

import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.runFile
import instacli.core.*
import instacli.util.Yaml
import java.io.File

class FileCommandInfo(val scriptFile: File) : CommandInfo, CommandHandler(asCliCommand(scriptFile.name)) {

    override val name: String = asCliCommand(scriptFile.name)
    override val summary: String  by lazy { findSummary() }

    val scriptNodes: List<JsonNode> by lazy { Yaml.parse(scriptFile) }

    private fun findSummary(): String {
        for (node in scriptNodes) {
            if (node.has("Meta")) {
                val value = node.get("Meta")?.get("summary")?.textValue()
                if (value != null) {
                    return value
                }
            }
        }
        return asScriptCommand(name)
    }

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        return runFile(scriptFile, data) // TODO untangle all the 'runFile' methods
    }
}