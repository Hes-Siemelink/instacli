package instacli.script

import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.ScriptInfo
import instacli.commands.ScriptInfoData
import instacli.util.Yaml

data class Command(val name: String, val data: JsonNode)

class Script(val commands: List<Command>) {

    val info: ScriptInfoData? by lazy {
        getScriptInfo()
    }

    fun runScript(context: ScriptContext): JsonNode? {
        var output: JsonNode? = null

        for (command in commands) {
            val handler = context.getCommandHandler(command.name)
            val evaluatedData = eval(command.data.deepCopy(), context)

            output = runCommand(handler, evaluatedData, context) ?: output
        }

        return output
    }

    private fun getScriptInfo(): ScriptInfoData? {
        val command = commands.find { it.name == ScriptInfo.name } ?: return null

        return ScriptInfoData.from(command.data)
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
