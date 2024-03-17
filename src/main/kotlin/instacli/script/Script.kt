package instacli.script

import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.OnError
import instacli.commands.OnErrorType
import instacli.commands.ScriptInfo
import instacli.commands.ScriptInfoData
import instacli.util.Yaml
import instacli.util.toDomainObject

data class Command(val name: String, val data: JsonNode)

class Script(val commands: List<Command>) {

    val info: ScriptInfoData? by lazy {
        getScriptInfo()
    }

    fun runScript(context: ScriptContext): JsonNode? {
        var output: JsonNode? = null

        for (command in commands) {
            val handler = context.getCommandHandler(command.name)

            if (context.error != null && !(handler == OnError || handler == OnErrorType)) {  // TODO: introduce interface 'ErrorHandler'
                continue
            }
            
            try {
                output = runCommand(handler, command.data, context) ?: output
            } catch (e: InstacliCommandError) {
                context.error = e
            }
        }

        context.error?.let { throw it }

        return output
    }

    private fun getScriptInfo(): ScriptInfoData? {
        val command = commands.find { it.name == ScriptInfo.name } ?: return null

        return command.data.toDomainObject(ScriptInfoData::class)
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
