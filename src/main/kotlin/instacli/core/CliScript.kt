package instacli.core

import com.fasterxml.jackson.databind.JsonNode

class CliScript(
    private val script: List<JsonNode>,
    val context: ScriptContext
) {

    fun run(): JsonNode? {
        val statements = script.map { scriptNode -> toCommands(scriptNode) }.flatten()
        return runScript(statements, context)
    }
}