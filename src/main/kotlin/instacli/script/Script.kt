package instacli.script

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.commands.ScriptInfo
import instacli.util.Yaml

data class Command(val name: String, val data: JsonNode)

class Script(val commands: List<Command>) {

    val description: String?
            by lazy { findDescription() }
    val input: Command?
            by lazy { commands.first { it.name == "Input" } }

    fun runScript(context: ScriptContext): JsonNode? {
        var output: JsonNode? = null

        for (command in commands) {
            val handler = context.getCommandHandler(command.name)
            val evaluatedData = eval(command.data, context)

            output = runCommand(handler, evaluatedData, context)
        }

        return output
    }

    private fun findDescription(): String? {
        for (command in commands) {
            if (command.name == ScriptInfo().name) {
                val info = CliScriptInfo.from(command.data)
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

        fun from(data: ObjectNode): Script {
            return from(listOf(data))
        }

        fun from(source: String): Script {
            return from(Yaml.parse(source) as ObjectNode)
        }
    }
}

private fun toCommandList(script: List<JsonNode>): List<Command> {
    return script.map { scriptNode -> toCommandList(scriptNode) }.flatten()
}

private fun toCommandList(scriptNode: JsonNode): List<Command> {
    return scriptNode.fields().asSequence().map { Command(it.key, it.value) }.toList()
}

class Break(val output: JsonNode) : Exception()

class CliScriptInfo() : CommandInfo {

    override var name: String = ""
    override var description: String = ""

    constructor(textValue: String) : this() {
        description = textValue
    }

    companion object {
        fun from(data: JsonNode): CliScriptInfo {
            return Yaml.mapper.treeToValue(data, CliScriptInfo::class.java)
        }
    }
}