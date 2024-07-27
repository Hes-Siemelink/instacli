package instacli.commands.schema

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.networknt.schema.JsonSchema
import instacli.language.*
import instacli.language.types.BuiltinTypes
import instacli.language.types.Type
import instacli.language.types.TypeDefinition
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
            validateWithType(json, it, context)
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

private fun validateWithType(data: JsonNode, typeInfo: JsonNode, context: ScriptContext) {

    val type = getType(context, typeInfo)

    val messages = type.validate(data)

    if (messages.isNotEmpty()) {
        val validationErrors = messages.map {
            TextNode(it)
        }.toJson()

        throw InstacliCommandError("Type validation", "Type validation errors", validationErrors)
    }
}

private fun getType(context: ScriptContext, typeInfo: JsonNode): Type {
    if (typeInfo is TextNode) {
        val typeName = typeInfo.textValue()
        return BuiltinTypes.types[typeName]
            ?: context.getType(typeName)
            ?: throw InstacliCommandError("Unknown type:  ${typeInfo.textValue()}")
    }

    return typeInfo.toDomainObject(TypeDefinition::class)
}
