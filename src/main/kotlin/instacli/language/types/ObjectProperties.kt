package instacli.language.types

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.TextNode
import instacli.language.InstacliCommandError

data class ObjectProperties(
    @get:JsonAnyGetter
    val properties: Map<String, JsonNode> = mutableMapOf()
) : Type {

    override fun validate(data: JsonNode): List<String> {
        val messages = mutableListOf<String>()

        for ((field, value) in data.fields()) {
            if (field in properties.keys) {
                val typeInfo = properties[field]
                val type = BuiltinTypes.types[typeInfo?.textValue()]
                    ?: throw InstacliCommandError("Unknown type:  ${typeInfo?.textValue()}")
                messages.addAll(type.validate(value))
            }
        }

        return messages
    }
}

@JsonDeserialize(using = TypeReferenceDeserializer::class)
data class TypeReference(
    val name: String? = null,
    val parameterData: ParameterData? = null
)

class TypeReferenceDeserializer : JsonDeserializer<TypeReference>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): TypeReference {
        System.err.println(parser.parsingContext.currentValue) // Check if it gives us current type without having to parse the entire tree

        val node: JsonNode = parser.getCodec().readTree(parser)
        return when (node) {
            is TextNode -> {
                TypeReference(name = node.textValue())
            }

            else -> {
//                TypeReference(parameterData = node.toDomainObject(ParameterData::class))
                TypeReference(parameterData = parser.readValueAsTree())
            }
        }
    }

}