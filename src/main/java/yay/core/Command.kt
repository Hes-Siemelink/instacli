package yay.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode

abstract class CommandHandler(open val name: String) {

    open fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        when (data) {
            is ValueNode -> {
                return if (this is ValueHandler) {
                    try {
                        execute(data, context)
                    } catch (e: Exception) {
                        throw ScriptException("An error occurred evaluating this command:", getCommand(data), e);
                    }
                } else {
                    throw ScriptException("Command: '$name' does not handle simple text content.", getCommand(data))
                }
            }

            is ObjectNode -> {
                return if (this is ObjectHandler) {
                    try {
                        execute(data, context)
                    } catch (e: Exception) {
                        throw ScriptException("An error occurred evaluating this command:", getCommand(data), e);
                    }
                } else {
                    throw ScriptException("Command '$name' does not handle object content.", getCommand(data))
                }
            }

            is ArrayNode -> {
                return if (this is ArrayHandler) {
                    try {
                        execute(data, context)
                    } catch (e: Exception) {
                        throw ScriptException("An error occurred evaluating this command:", getCommand(data), e);
                    }
                } else {
                    throw ScriptException("Command '$name' does not handle array content.", getCommand(data))
                }
            }

            else -> throw ScriptException(
                "Unknown content type ${data.javaClass.simpleName} for command '$name'",
                getCommand(data)
            )
        }
    }

    fun getParameter(data: JsonNode, parameter: String): JsonNode {
        return data.get(parameter) ?: throw ScriptException(
            "Command '$name' needs '$parameter' field.",
            getCommand(data)
        )
    }

    fun getCommand(data: JsonNode): JsonNode {
        val node = ObjectNode(JsonNodeFactory.instance);
        node.set<JsonNode>(name, data)
        return node
    }
}


interface ValueHandler {
    fun execute(data: ValueNode, context: ScriptContext): JsonNode?
}

interface ObjectHandler {
    fun execute(data: ObjectNode, context: ScriptContext): JsonNode?
}

interface ArrayHandler {
    fun execute(data: ArrayNode, context: ScriptContext): JsonNode?
}

interface DelayedVariableResolver

//
// Helper functions
//

fun objectNode(key: String, value: String): ObjectNode {
    return ObjectNode(JsonNodeFactory.instance).put(key, value)
}
