package instacli.script

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.util.objectNode


fun runCommand(
    handler: CommandHandler,
    rawData: JsonNode,
    context: ScriptContext
): JsonNode? {

    return if (rawData is ArrayNode && !handler.handlesLists()) {
        runCommandOnList(handler, rawData, context)
    } else {
        runSingleCommand(handler, rawData, context)
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

        val result: JsonNode? = handleCommand(handler, data, context)

        if (result != null) {
            context.variables[OUTPUT_VARIABLE] = result
        }
        return result
    } catch (e: InstacliException) {
        e.data ?: run {
            e.data = asCommand(handler, data)
        }
        throw e
    }
}

fun handleCommand(handler: CommandHandler, data: JsonNode, context: ScriptContext): JsonNode? {
    try {
        return when {

            handler is AnyHandler -> {
                handler.execute(data, context)
            }

            handler is ValueHandler && data is ValueNode -> {
                handler.execute(data, context)
            }

            handler is ObjectHandler && data is ObjectNode -> {
                handler.execute(data, context)
            }

            handler is ArrayHandler && data is ArrayNode -> {
                handler.execute(data, context)
            }

            else -> throw CommandFormatException("Command '${handler.name}' does not handle content type ${data.javaClass.simpleName}")
        }
    } catch (a: Break) {
        throw a
    } catch (e: InstacliErrorCommand) {
        throw e
    } catch (e: InstacliException) {
        e.data = asCommand(handler, data)
        throw e
    } catch (e: Exception) {
        throw InstacliInternalException("", asCommand(handler, data), e)
    }
}

fun asCommand(handler: CommandHandler, data: JsonNode): JsonNode {
    val node = objectNode()
    node.set<JsonNode>(handler.name, data)
    return node
}
