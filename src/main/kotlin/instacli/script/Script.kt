package instacli.script

import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.ScriptInfo
import instacli.commands.ScriptInfoData
import instacli.util.Yaml

data class Command(val name: String, val data: JsonNode)

class Script(val commands: List<Command>) {

    val description: String?
            by lazy { findDescription() }
    val input: Command?
            by lazy { commands.first { it.name == "Input" } } // FIXME Use ScriptInfo and define a test for the --help option

    fun runScript(context: ScriptContext): JsonNode? {
        var output: JsonNode? = null

        for (command in commands) {
            val handler = context.getCommandHandler(command.name)
            val evaluatedData = eval(command.data.deepCopy(), context)

            output = runCommand(handler, evaluatedData, context) ?: output
        }

        return output
    }

    private fun findDescription(): String? {
        for (command in commands) {
            if (command.name == ScriptInfo().name) {
                val info = ScriptInfoData.from(command.data)
                if (info.description.isNotEmpty()) {
                    return info.description
                }
            }
        }
        return null
    }

    companion object {
        fun from(script: List<JsonNode>): Script {
            return Script(toCommandList(script))
        }

        fun from(data: JsonNode): Script {
            return Script(toCommandList(data))
        }

        fun from(content: String): Script {
            return from(Yaml.parseAsFile(content))
        }
    }
}

private fun toCommandList(script: List<JsonNode>): List<Command> {
    return script.map { scriptNode -> toCommandList(scriptNode) }.flatten()
}

private fun toCommandList(scriptNode: JsonNode): List<Command> {
    return scriptNode.fields().asSequence().map { Command(it.key, it.value) }.toList()
}

fun JsonNode.runScript(context: ScriptContext): JsonNode? {
    return Script.from(this).runScript(context)
}

class Break(val output: JsonNode) : Exception()
