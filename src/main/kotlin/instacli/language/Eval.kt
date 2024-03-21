package instacli.language

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import instacli.util.JsonProcessor

fun eval(data: JsonNode, context: ScriptContext): JsonNode {
    return Evaluator(context).process(data)
}

private class Evaluator(val context: ScriptContext) : JsonProcessor() {

    override fun processObject(node: ObjectNode): JsonNode {

        for ((key, data) in node.fields()) {
            val evaluatedData = process(data)
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
}