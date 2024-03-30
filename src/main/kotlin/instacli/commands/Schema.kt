package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.networknt.schema.JsonSchema
import instacli.language.*
import instacli.util.Json
import instacli.util.JsonSchemas
import instacli.util.toJson

object Validate : CommandHandler("Validate"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val json = data.getParameter("data")
        val schema = getSchema(data.getParameter("schema"), context)

        val messages = schema.validate(json)

        return if (messages.isEmpty()) {
            TextNode("valid")
        } else {
            val validationErrors = messages.map {
                Json.newObject(it.code, it.message)
            }.toJson()

            throw InstacliCommandError("Schema validation error", "Schema validation error", validationErrors)
        }
    }
}

private fun getSchema(data: JsonNode, context: ScriptContext): JsonSchema {
    return if (data is TextNode) {
        val location = context.scriptDir.resolve(data.textValue())
        JsonSchemas.factory.getSchema(location.toUri())
    } else {
        JsonSchemas.factory.getSchema(data)
    }
}

private fun JsonNode.validateWithSchema(schema: JsonSchema) {
    val messages = schema.validate(this)
    if (messages.isNotEmpty()) {
        val schemaName = schema.toString()
        throw CommandFormatException("Schema validation errors according to \"${schemaName}\":\n$messages")
    }
}