package instacli.script

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.util.JsonSchemas

abstract class CommandHandler(open val name: String) {


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

fun interface AnyHandler {
    fun execute(data: JsonNode, context: ScriptContext): JsonNode?
}

/**
 * Marker interface that indicates that variables should not be expanded.
 * The CommandHandler will expand the variables at the time needed.
 */
interface DelayedVariableResolver

