package instacli.script

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.util.JsonSchemas
import instacli.util.objectNode

abstract class CommandHandler(open val name: String) {

    open fun handleCommand(data: JsonNode, context: ScriptContext): JsonNode? {
        try {
            return when (data) {
                is ValueNode -> {
                    handleValueNode(data, context)
                }

                is ObjectNode -> {
                    handleObjectNode(data, context)
                }

                is ArrayNode -> {
                    handleArrayNode(data, context)
                }

                else -> throw IllegalArgumentException("Unknown content type ${data.javaClass.simpleName} for command '$name'")
            }
        } catch (a: Break) {
            throw a
        } catch (e: InstacliException) {
            e.data = getCommand(data)
            throw e
        } catch (e: Exception) {
            throw InstacliInternalException("", getCommand(data), e)
        }
    }

    private fun handleValueNode(data: ValueNode, context: ScriptContext): JsonNode? {
        if (this is ValueHandler) {
            return execute(data, context)
        }

        throw CommandFormatException("Command: '$name' does not handle simple text content.")
    }

    private fun handleObjectNode(data: ObjectNode, context: ScriptContext): JsonNode? {
        if (this is ObjectHandler) {
            return execute(data, context)
        }

        throw CommandFormatException("Command '$name' does not handle object content.")
    }

    private fun handleArrayNode(data: ArrayNode, context: ScriptContext): JsonNode? {
        if (this is ArrayHandler) {
            return execute(data, context)
        }

        throw CommandFormatException("Command '$name' does not handle array content.")
    }

    fun getParameter(data: JsonNode, parameter: String): JsonNode {
        return data[parameter] ?: throw CommandFormatException("Command '$name' needs '$parameter' field.")
    }

    fun getTextParameter(data: JsonNode, parameter: String): String {
        val value = getParameter(data, parameter)
        if (value !is ValueNode) {
            throw CommandFormatException("Parameter $parameter in command '$name' needs to be a text value.")
        }
        return value.textValue()
    }

    fun getCommand(data: JsonNode): JsonNode {
        val node = objectNode()
        node.set<JsonNode>(name, data)
        return node
    }

    fun handlesLists(): Boolean {
        return when (this) {
            is ArrayHandler -> {
                true
            }

            is ObjectHandler, is ValueHandler -> {
                false
            }

            else -> true
        }
    }

    open fun validate(data: JsonNode) {
        val schema = JsonSchemas.getSchema(name) ?: return

        val messages = schema.validate(data)
        if (messages.isNotEmpty()) {
            throw CommandFormatException("Schema validation errors:\n$messages")
        }
    }
}


fun interface ValueHandler {
    fun execute(data: ValueNode, context: ScriptContext): JsonNode?
}

fun interface ObjectHandler {
    fun execute(data: ObjectNode, context: ScriptContext): JsonNode?
}

fun interface ArrayHandler {
    fun execute(data: ArrayNode, context: ScriptContext): JsonNode?
}

/**
 * Marker interface that indicates that variables should not be expanded.
 * The CommandHandler will expand the variables at the time needed.
 */
interface DelayedVariableResolver

