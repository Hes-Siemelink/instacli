package instacli.script.execution

import com.fasterxml.jackson.databind.JsonNode

class CliScript(private val script: List<JsonNode>) {

    fun run(context: ScriptContext): JsonNode? {
        val statements = script.map { scriptNode -> toCommands(scriptNode) }.flatten()
        return runScript(statements, context)
    }
}