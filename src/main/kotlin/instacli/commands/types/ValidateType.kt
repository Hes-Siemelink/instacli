package instacli.commands.schema

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import instacli.language.*
import instacli.language.types.Type
import instacli.language.types.TypeReference
import instacli.language.types.resolveTypes
import instacli.util.*

object ValidateType : CommandHandler("Validate type", "instacli/schema"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val json = data.getParameter("item")
        val typeData = data.getParameter("type")
        val type = getType(typeData, context)

        // Type validation throws exception when invalid
        validate(json, type)

        return null
    }
}

private fun validate(data: JsonNode, type: Type) {

    val messages = type.validate(data)

    if (messages.isNotEmpty()) {
        val validationErrors = messages.map {
            TextNode(it)
        }.toJson()

        throw InstacliCommandError("Type validation", "Type validation errors", validationErrors)
    }
}

private fun getType(typeData: JsonNode, context: ScriptContext): Type {

    val typeRef = typeData.toDomainObject(TypeReference::class)

    val type = typeRef.resolveTypes(context)

    return type
//    if (typeData is TextNode) {
//        val typeName = typeData.textValue()
//        return BuiltinTypes.types[typeName]
//            ?: context.getType(typeName)
//            ?: throw InstacliCommandError("Unknown type:  ${typeData.textValue()}")
//    }
//
}
