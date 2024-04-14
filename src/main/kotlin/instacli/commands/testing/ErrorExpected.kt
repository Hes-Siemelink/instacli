package instacli.commands.testing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.*

object ErrorExpected : CommandHandler("Error expected", "instacli/testing"), ValueHandler, ArrayHandler, ErrorHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {

        if (context.error == null) {
            throw InstacliCommandError(data.textValue())
        }

        context.error = null

        return null
    }

    override fun execute(data: ArrayNode, context: ScriptContext): JsonNode? {
        throw CommandFormatException("Arrays are not allowed in 'Error expected'")
    }
}