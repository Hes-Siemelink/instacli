package instacli.script.execution

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode

class CliScript(private val script: List<Command>) {

    fun run(context: ScriptContext): JsonNode? {
        var output: JsonNode? = null

        for (command in script) {
            val handler = context.getCommandHandler(command.name)
            output = command.run(handler, context)
        }

        return output
    }

    companion object {
        fun from(script: List<JsonNode>): CliScript {
            return CliScript(toCommandList(script))
        }

        fun from(data: ObjectNode): CliScript {
            return from(listOf(data))
        }
    }
}

private fun toCommandList(script: List<JsonNode>): List<Command> {
    return script.map { scriptNode -> toCommandList(scriptNode) }.flatten()
}

private fun toCommandList(scriptNode: JsonNode): List<Command> {
    return scriptNode.fields().asSequence().map { Command(it.key, it.value) }.toList()
}
