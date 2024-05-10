package instacli.commands.schema

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.module.kotlin.contains
import com.networknt.schema.JsonSchema
import instacli.language.*
import instacli.language.types.BuiltinTypes
import instacli.language.types.ObjectProperties
import instacli.language.types.Type
import instacli.util.*

object Validate : CommandHandler("Validate", "instacli/schema"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode {
        val json = data.getParameter("data")

        // Schema validation
        data.get("schema")?.let {
            val schema = it.getSchema(context)
            json.validateWithSchema(schema)
        }

        // Type validation
        data.get("type")?.let {
            validateWithType(json, it)
        }

        return BooleanNode.TRUE
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

//
// Types
//

private fun validateWithType(data: JsonNode, typeInfo: TypeNode) {

    val type = typeInfo.getType()

    val messages = type.validate(data)

    if (messages.isNotEmpty()) {
        val validationErrors = messages.map {
            TextNode(it)
        }.toJson()

        throw InstacliCommandError("Type validation", "Type validation errors", validationErrors)
    }
}

typealias TypeNode = JsonNode

private fun TypeNode.getType(): Type {
    if (this is TextNode) {
        return BuiltinTypes.types[textValue()] ?: throw InstacliCommandError("Unknown type:  ${textValue()}")
    }

    if (contains("object")) {
        return get("object").toDomainObject(ObjectProperties::class)
    }

    throw InstacliCommandError(
        type = "Unknown type",
        message = "Unknown type:  ${toDisplayYaml()}"
    )
}
