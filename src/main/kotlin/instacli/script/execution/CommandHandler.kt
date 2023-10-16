package instacli.script.execution

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.util.objectNode

abstract class CommandHandler(open val name: String) {

    open fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
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
        } catch (e: Exception) {
            throw CliScriptException("In command\n", getCommand(data), e)
        }
    }

    private fun handleValueNode(data: ValueNode, context: ScriptContext): JsonNode? {
        if (this is ValueHandler) {
            return execute(data, context)
        }

        throw CliScriptException("Command: '$name' does not handle simple text content.", getCommand(data))
    }

    private fun handleObjectNode(data: ObjectNode, context: ScriptContext): JsonNode? {
        if (this is ObjectHandler) {
            return execute(data, context)
        }

        throw CliScriptException("Command '$name' does not handle object content.", getCommand(data))
    }

    private fun handleArrayNode(data: ArrayNode, context: ScriptContext): JsonNode? {
        if (this is ArrayHandler) {
            return execute(data, context)
        }

        throw CliScriptException("Command '$name' does not handle array content.", getCommand(data))
    }

    fun getParameter(data: JsonNode, parameter: String): JsonNode {
        return data[parameter] ?: throw CliScriptException(
            "Command '$name' needs '$parameter' field.",
            getCommand(data)
        )
    }

    fun getTextParameter(data: JsonNode, parameter: String): String {
        val value = getParameter(data, parameter)
        if (value !is ValueNode) {
            throw CliScriptException("Parameter $parameter in command '$name' needs to be a text value.", getCommand(data))
        }
        return value.textValue()
    }

    fun getCommand(data: JsonNode): JsonNode {
        val node = objectNode()
        node.set<JsonNode>(name, data)
        return node
    }

    fun handlesListsItself(): Boolean {
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

