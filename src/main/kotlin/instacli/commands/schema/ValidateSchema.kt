package instacli.commands.schema

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.networknt.schema.JsonSchema
import instacli.language.*
import instacli.util.*

object ValidateSchema : CommandHandler("Validate schema", "instacli/schema"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val json = data.getParameter("data")

        // Schema validation
        data.get("schema")?.let {
            val schema = it.getSchema(context)
            json.validateWithSchema(schema)
        }

        return null
    }
}


//
// Schema support
//

private fun JsonNode.validateWithSchema(schema: JsonSchema) {

    val messages = schema.validate(this)

    if (messages.isNotEmpty()) {
        val validationErrors = messages.map {
            Json.newObject(it.code, it.message)
        }.toJson()

        throw InstacliCommandError("Schema validation error", "Schema validation error", validationErrors)
    }
}

private fun JsonNode.getSchema(context: ScriptContext): JsonSchema {

    return if (this is TextNode) {
        val location = context.scriptDir.resolve(textValue())
        JsonSchemas.factory.getSchema(location.toUri())
    } else {
        JsonSchemas.factory.getSchema(this)
    }
}
