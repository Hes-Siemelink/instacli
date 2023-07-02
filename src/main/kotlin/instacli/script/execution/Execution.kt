package instacli.script.execution

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode

class Command(val name: String, val data: JsonNode)

fun toCommands(scriptNode: JsonNode): List<Command> {
    return scriptNode.fields().asSequence().map { Command(it.key, it.value) }.toList()
}

fun runScript(script: List<Command>, context: ScriptContext): JsonNode? {
    var output: JsonNode? = null

    for (command in script) {
        val handler = context.getCommandHandler(command.name)
        output = runCommand(handler, command.data, context)
    }

    return output
}

fun runCommand(
        handler: CommandHandler,
        rawData: JsonNode,
        context: ScriptContext
): JsonNode? {

    if (rawData is ArrayNode && !handlesListItself(handler)) {
        return runCommandOnList(handler, rawData, context)
    } else {
        return runSingleCommand(handler, rawData, context)
    }
}

fun handlesListItself(handler: CommandHandler): Boolean {
    if (handler is ArrayHandler) {
        return true
    } else if (handler is ObjectHandler || handler is ValueHandler) {
        return false
    }

    return true
}

private fun runCommandOnList(
        handler: CommandHandler,
        dataList: ArrayNode,
        context: ScriptContext
): ArrayNode? {

    val output = ArrayNode(JsonNodeFactory.instance)

    for (data in dataList) {
        val result = runSingleCommand(handler, data, context)
        if (result != null) {
            output.add(result)
        }
    }

    return if (output.isEmpty) {
        null
    } else {
        context.variables["output"] = output
        output
    }
}

private fun runSingleCommand(
        handler: CommandHandler,
        rawData: JsonNode,
        context: ScriptContext
): JsonNode? {

    try {
        val data = if (handler is DelayedVariableResolver) rawData else resolveVariables(rawData, context.variables)
        val result: JsonNode? = handler.execute(data, context)
        if (result != null) {
            context.variables["output"] = result
        }
        return result
    } catch (e: CliScriptException) {
        e.data ?: run {
            e.data = ObjectNode(JsonNodeFactory.instance, mapOf(handler.name to rawData))
        }
        throw e
    }
}
