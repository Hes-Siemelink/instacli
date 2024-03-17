package instacli.language

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.util.Json


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
        data = if (handler is DelayedResolver) {
            rawData
        } else {
            rawData.resolve(context)
        }

        handler.validate(data)

        val result: JsonNode? = handleCommand(handler, data, context)

        if (result != null) {
            context.variables[OUTPUT_VARIABLE] = result
        }
        return result
    } catch (e: InstacliLanguageException) {
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
    } catch (e: InstacliCommandError) {
        throw e
    } catch (e: InstacliLanguageException) {
        e.data = asCommand(handler, data)
        throw e
    } catch (e: Exception) {
        throw InstacliImplementationException("", asCommand(handler, data), e)
    }
}

fun asCommand(handler: CommandHandler, data: JsonNode): JsonNode {
    val node = Json.newObject()
    node.set<JsonNode>(handler.name, data)
    return node
}

fun JsonNode.resolve(context: ScriptContext, variables: Map<String, JsonNode> = context.variables): JsonNode {
    // TODO: Make this more efficient so we are not copying the tree like three times
    val evaluatedData = eval(this.deepCopy(), context)
    return evaluatedData.resolveVariables(variables)
}
