package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import hes.yak.Command
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

class Join : Command {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        if (data !is ObjectNode) throw ScriptException("Join only takes object content, not array or list.\n$data")

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