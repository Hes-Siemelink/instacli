package hes.yak

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory

class Statement(val command: String, val data: JsonNode)

fun toStatements(scriptNode: JsonNode): List<Statement> {
    return scriptNode.fields().asSequence().map { Statement(it.key, it.value) }.toList()
}

fun runScript(script: List<Statement>, context: ScriptContext): JsonNode? {
    var output: JsonNode? = null

    for (statement in script) {
        val handler = context.getCommandHandler(statement.command)
        output = runCommand(handler, statement.data, context)
    }

    return output
}

fun runCommand(
    handler: CommandHandler,
    rawData: JsonNode,
    context: ScriptContext
): JsonNode? {

    if (handler is ListProcessor && rawData is ArrayNode) {
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
        context.variables["output"] = output
        output
    }
}

private fun runSingleCommand(
    handler: CommandHandler,
    rawData: JsonNode,
    context: ScriptContext
): JsonNode? {

    val data = if (handler is DelayedVariableResolver) rawData else resolveVariables(rawData, context.variables)
    val result: JsonNode? = handler.execute(data, context)
    if (result != null) {
        context.variables["output"] = result
    }
    return result
}
