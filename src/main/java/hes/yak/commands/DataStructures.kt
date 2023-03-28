package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import hes.yak.CommandHandler
import hes.yak.ScriptContext
import hes.yak.ScriptException

class Join : CommandHandler {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        if (data !is ObjectNode) throw ScriptException("Join takes object content, not array or text.\n$data")

        for (variable in data.fields()) {
            join(variable.key, variable.value, context.variables)
        }

        return null
    }

    private fun join(varName: String, data: JsonNode, variables: MutableMap<String, JsonNode>) {
        val content = variables[varName]

        if (content == null) {
            variables[varName] = data
            return
        }

        if (content is ValueNode) {
            throw ScriptException("Can't update value variable $varName with 'Join' command")
        }

        if (content is ArrayNode) {
            if (data is ArrayNode) {
                content.addAll(data)
            } else {
                content.add(data)
            }
        }

        if (content is ObjectNode) {
            if (data is ObjectNode) {
                content.setAll<ObjectNode>(data)
            } else {
                throw ScriptException("Can't update variable $varName that has object content with text or array.")
            }
        }
    }
}

class Merge : CommandHandler {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode {
        if (data !is ArrayNode) throw ScriptException("Merge only takes array content, not object or text.\n$data")

        var result: JsonNode = TextNode("dummy")
        var first = true

        for (item in data) {

            // Initialize result node based on first entry
            if (first) {
                if (item is ValueNode) {
                    result = ArrayNode(JsonNodeFactory.instance)
                    addValue(result, item)
                } else {
                    result = item
                }
                first = false
                continue
            }

            // Append to existing node
            if (result is ObjectNode) {
                if (item is ObjectNode) {
                    result.setAll<ObjectNode>(item)
                } else {
                    throw ScriptException("Can't merge array or text content with object:\nCurrent: $result\nAdding: $item")
                }
            } else if (result is ArrayNode) {
                if (item is ArrayNode) {
                    result.addAll(item)
                } else {
                    result.add(item)
                }
            }
        }

        return result
    }

    private fun addValue(
        array: ArrayNode,
        item: ValueNode
    ) {
        if (item.isTextual) array.add(item.textValue())
        else if (item.isInt) array.add(item.intValue())
        else if (item.isBoolean) array.add(item.booleanValue())
        else array.add(item.textValue())
    }

}