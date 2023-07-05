package instacli.script.execution

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.script.commands.ScriptInfoHandler
import instacli.util.Yaml

class CliScript(val commands: List<Command>) {

    val description: String? by lazy { findDescription() }

    fun run(context: ScriptContext): JsonNode? {
        var output: JsonNode? = null

        for (command in commands) {
            val handler = context.getCommandHandler(command.name)
            output = command.run(handler, context)
        }

        return output
    }

    private fun findDescription(): String? {
        for (command in commands) {
            if (command.name == ScriptInfoHandler().name) {
                val info = CliScriptInfo.from(command.data)
                if (info.description.isNotEmpty()) {
                    return info.description
                }
            }
        }
        return null
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

class CliScriptInfo : CommandInfo {

    override var name: String = ""
    override var description: String = ""

    companion object {
        fun from(data: JsonNode): CliScriptInfo {
            return Yaml.mapper.treeToValue(data, CliScriptInfo::class.java)
        }
    }
}

const val TYPE_STRING = "string"

class InputInfo {

    @JsonAnySetter
    var parameters: Map<String, InputParameterInfo> = mutableMapOf()

    companion object {
        fun from(data: JsonNode): InputInfo {
            return Yaml.mapper.treeToValue(data, InputInfo::class.java)
        }
    }
}

data class InputParameterInfo(
    var description: String = "",
    val type: String = TYPE_STRING,
    val default: String = ""
) {
    constructor(textValue: String) : this(description = textValue)

}

