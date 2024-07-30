package instacli.commands.testing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.*
import instacli.util.toDisplayYaml

object ExpectedError :
    CommandHandler("Expected error", "instacli/testing"),
    ErrorHandler,
    ValueHandler,
    ArrayHandler,
    ObjectHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {

        if (context.error == null) {
            throw MissingExpectedError(data.textValue())
        }

        context.error = null

        return null
    }

    override fun execute(data: ArrayNode, context: ScriptContext): JsonNode? {
        throw CommandFormatException("Arrays are not allowed in 'Expected error'")
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        for ((key, _) in data.fields()) {
            if (key == "any" || key == context.error?.error?.type) {
                context.error = null
                return null
            }
        }

        if (context.error == null) {
            throw MissingExpectedError(data.toDisplayYaml())
        } else {
            throw MissingExpectedError("${data.toDisplayYaml()}\nGot instead: ${context.error?.error} ")
        }
    }
}

class MissingExpectedError(message: String) : InstacliCommandError(message)