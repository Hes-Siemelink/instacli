package instacli.language.types

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.TextNode
import instacli.language.CliScriptingException
import instacli.language.ScriptContext
import instacli.util.toDomainObject

@JsonDeserialize(using = TypeReferenceDeserializer::class)
data class TypeReference(
    val name: String? = null,
    val definition: TypeDefinition? = null,
) {
    constructor(definition: TypeDefinition?) : this(null, definition)
}

fun TypeReference.resolveTypes(context: ScriptContext): TypeDefinition {
    return when {
        name != null -> {
            (context.getType(name) ?: throw CliScriptingException("Type not found: $name")) as TypeDefinition
        }

        definition != null -> {
            definition.resolveTypes(context)

        }

        else -> {
            throw IllegalStateException("TypeReference must have either a name or a definition")
        }
    }
}

fun TypeDefinition.toTypeReference(): TypeReference {
    return TypeReference(this)
}

class TypeReferenceDeserializer : JsonDeserializer<TypeReference>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): TypeReference {
        val node: JsonNode = parser.getCodec().readTree(parser)
        return when {
            node is TextNode -> {
                TypeReference(name = node.textValue())
            }

            node.has("type") && node["type"] is TextNode -> {
                TypeReference(name = node["type"].textValue())
            }

            else -> {
                TypeReference(definition = node.toDomainObject(TypeDefinition::class))
            }
        }
    }
}