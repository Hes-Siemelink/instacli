package instacli.script.execution

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode

class Command(val name: String, val data: JsonNode) {
    fun run(handler: CommandHandler, context: ScriptContext): JsonNode? {
        return runCommand(handler, data, context)
    }
}

fun runCommand(
    handler: CommandHandler,
    rawData: JsonNode,
    context: ScriptContext
): JsonNode? {

    if (rawData is ArrayNode && !handler.handlesListsItself()) {
        return runCommandOnList(handler, rawData, context)
    } else {
        return runSingleCommand(handler, rawData, context)
    }
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
        context.variables[OUTPUT_VARIABLE] = output
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

        handler.validate(data)

        val result: JsonNode? = handler.execute(data, context)
        if (result != null) {
            context.variables[OUTPUT_VARIABLE] = result
        }
        return result
    } catch (e: CliScriptException) {
        e.data ?: run {
            e.data = ObjectNode(JsonNodeFactory.instance, mapOf(handler.name to rawData))
        }
        throw e
    }
}