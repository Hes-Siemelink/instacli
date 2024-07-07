package instacli.language.types

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.TextNode

@JsonDeserialize(using = TypeReferenceDeserializer::class)
data class TypeReference(
    val name: String? = null,
    val definition: TypeDefinition? = null,
)

class TypeReferenceDeserializer : JsonDeserializer<TypeReference>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): TypeReference {
        val node: JsonNode = parser.getCodec().readTree(parser)
        return when (node) {
            is TextNode -> {
                TypeReference(name = node.textValue())
            }

            else -> {
                TypeReference(definition = parser.readValueAsTree())
            }
        }
    }
}