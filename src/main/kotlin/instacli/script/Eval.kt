package instacli.script

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*

fun eval(data: JsonNode, context: ScriptContext): JsonNode {
    return when (data) {
        is ValueNode -> data
        is ArrayNode -> {
            val result = data.map { eval(it, context) }
            ArrayNode(JsonNodeFactory.instance, result)
        }

        is ObjectNode -> {
            evalObject(data, context)
        }

        else -> throw IllegalStateException("Unknown type ${data.javaClass.name}")
    }
}

fun evalObject(node: ObjectNode, context: ScriptContext): JsonNode {
    for ((key, data) in node.fields()) {
        val evaluatedData = eval(data, context)
        node.set<JsonNode>(key, evaluatedData)
        if (key.startsWith(":")) {
            val name = key.substring(1)
            val handler = context.getCommandHandler(name)
            val result = runCommand(handler, evaluatedData, context)
            return result ?: TextNode("")
        }
    }
    return node
}
