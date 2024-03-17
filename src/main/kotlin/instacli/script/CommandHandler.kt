package instacli.script

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.util.validateWithSchema

abstract class CommandHandler(open val name: String) {

    fun handlesLists(): Boolean {
        return when (this) {
            is ArrayHandler, is AnyHandler -> {
                true
            }

            else -> false
        }
    }

    open fun validate(data: JsonNode) {
        data.validateWithSchema(name)
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

fun interface AnyHandler {
    fun execute(data: JsonNode, context: ScriptContext): JsonNode?
}

/**
 * Marker interface for commands that contain other commands, like Do or For each.
 * Indicates that variables should not be expanded and eval should not take place yet.
 */
interface DelayedResolver

fun JsonNode.getParameter(parameter: String): JsonNode {
    return this[parameter] ?: throw CommandFormatException("Expected field '$parameter'.")
}

fun JsonNode.getTextParameter(parameter: String): String {
    val value = this.getParameter(parameter)
    if (value !is ValueNode) {
        throw CommandFormatException("Field '$parameter' needs to be a text value.")
    }
    return value.textValue()
}
