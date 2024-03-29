package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.*
import instacli.util.Yaml
import instacli.util.toDisplayYaml
import instacli.util.toDomainObject

object ErrorCommand : CommandHandler("Error"), ValueHandler, ObjectHandler, ArrayHandler {
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

object OnError : CommandHandler("On error"), ObjectHandler, DelayedResolver, ErrorHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        runErrorHandling(data, context)
        return null
    }
}

object OnErrorType : CommandHandler("On error type"), ObjectHandler, DelayedResolver, ErrorHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        for ((key, value) in data.fields()) {
            if (key == "any" || key == context.error?.error?.type) {
                runErrorHandling(value, context)
                break
            }
        }

        return null
    }
}

// TODO: move this to 'Script'

private fun runErrorHandling(errorHandlingSection: JsonNode, context: ScriptContext) {
    val error = context.error ?: return

    context.variables["error"] = Yaml.mapper.valueToTree(error.error)
    context.error = null

    errorHandlingSection.runScript(context)

    context.variables.remove("error")
}

//
// Data model
//

class ErrorData {

    var message: String = "An error occurred"
    var type: String = "general"
    var data: JsonNode? = null

    constructor()
    constructor(message: String = "An error occurred", type: String = "general", data: JsonNode? = null) {
        this.message = message
        this.type = type
        this.data = data
    }
}
