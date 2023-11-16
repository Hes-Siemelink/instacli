package instacli.engine

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory

data class Command(val name: String, val data: JsonNode)

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

    var data = rawData
    try {
        data = if (handler is DelayedVariableResolver) rawData else resolveVariables(rawData, context.variables)

        handler.validate(data)

        val result: JsonNode? = handler.execute(data, context)

        if (result != null) {
            context.variables[OUTPUT_VARIABLE] = result
        }
        return result
    } catch (e: InstacliException) {
        e.data ?: run {
            e.data = handler.getCommand(data)
        }
        throw e
    }
}