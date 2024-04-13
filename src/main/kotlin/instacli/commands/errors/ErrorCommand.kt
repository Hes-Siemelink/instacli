package instacli.commands.errors

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.*
import instacli.util.toDisplayYaml
import instacli.util.toDomainObject

object ErrorCommand : CommandHandler("Error", "instacli/errors"), ValueHandler, ObjectHandler, ArrayHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        throw InstacliCommandError(data.toDisplayYaml())
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val errorData = data.toDomainObject(ErrorData::class)

        throw InstacliCommandError(errorData.message, errorData)
    }

    override fun execute(data: ArrayNode, context: ScriptContext): JsonNode? {
        // Prevent behavior of 'default list handler' for errors
        throw CommandFormatException("Error does not support lists")
    }
}