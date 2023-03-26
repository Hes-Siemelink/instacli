package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import hes.yak.Command
import hes.yak.ListProcessor
import hes.yak.ScriptContext
import hes.yak.ScriptException

class VariableAssignment : Command {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        for (variable in data.fields()) {
            context.variables[variable.key] = variable.value
        }
        return null;
    }
}

class VariableCommand(val name: String) : Command {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        context.variables[name] = data
        return null;
    }
}

class AssignOutput : Command, ListProcessor {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        if (data !is TextNode) throw ScriptException("Command 'As' only takes text.\n$data")

        if (!context.variables.containsKey("output")) throw ScriptException("Can't assign output variable because it is empty.")

        context.variables[data.asText()] = context.variables["output"]!!

        return null
    }
}

class Join : Command {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        if (data !is ObjectNode) throw ScriptException("Join only takes object content, not array or text.\n$data")

        for (variable in data.fields()) {
            join(variable.key, variable.value, context.variables)
        }

        return null
    }

    private fun join(varName: String, data: JsonNode, variables: Map<String, JsonNode>) {
        val content = variables[varName]

        if (content is TextNode) {
            throw ScriptException("Can't update text variable $varName with 'Join' command")
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

class Merge : Command {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        if (data !is ArrayNode) throw ScriptException("Merge only takes array content, not onject or text.\n$data")

        var result: JsonNode = TextNode("dummy")
        var first = true

        for (item in data) {

            // Initialize result node based on first entry
            if (first) {
                if (item is TextNode) {
                    result = ArrayNode(JsonNodeFactory.instance)
                    result.add(item.asText())
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

}