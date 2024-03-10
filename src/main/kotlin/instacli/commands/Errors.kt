package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.script.*
import instacli.util.Yaml
import instacli.util.toDisplayString
import instacli.util.toDomainObject

object ErrorCommand : CommandHandler("Error"), ValueHandler, ObjectHandler, ArrayHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        throw InstacliCommandError(data.toDisplayString())
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val errorData = data.toDomainObject(ErrorData::class)
        throw InstacliCommandError(errorData.message, errorData)
    }

    override fun execute(data: ArrayNode, context: ScriptContext): JsonNode? {
        throw CommandFormatException("Error does not support lists")
    }
}

object OnError : CommandHandler("On error"), ObjectHandler, DelayedVariableResolver {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        runErrorHandling(data, context)
        return null
    }
}

object OnErrorType : CommandHandler("On error type"), ObjectHandler, DelayedVariableResolver {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        for ((key, value) in data.fields()) {
            if (key == "any" || key == context.error?.data?.type) {
                runErrorHandling(value, context)
                break
            }
        }

        return null
    }
}

// TODO: move this to 'Script'

private fun runErrorHandling(errorHandlingSection: JsonNode, context: ScriptContext) {
    context.error?.data?.let {
        context.variables["error"] = Yaml.mapper.valueToTree(it)
    }
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
