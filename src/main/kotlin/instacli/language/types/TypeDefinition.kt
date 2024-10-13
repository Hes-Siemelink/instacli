package instacli.language.types

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import instacli.language.CommandFormatException
import instacli.language.ScriptContext

data class TypeDefinition(
    val base: String? = null,
    val properties: ObjectProperties? = null,
    @JsonProperty("list of")
    val listOf: TypeReference? = null,
) : Type {

    override fun validate(data: JsonNode): List<String> {

        return when (base) {

            "string" -> {
                if (data !is TextNode) {
                    return listOf("Data should be string but is ${data::class.simpleName}")
                }
                emptyList()
            }

            "boolean" -> {
                if (data !is BooleanNode) {
                    return listOf("Data should be boolean but is ${data::class.simpleName}")
                }
                emptyList()
            }

            "object" -> {
                if (data !is ObjectNode) {
                    return listOf("Data should be object but is ${data::class.simpleName}")
                }
                properties?.validate(data) ?: emptyList()
            }

            "array" -> {
                if (data !is ArrayNode) {
                    return listOf("Data should be array but is ${data::class.simpleName}")
                }
                data.flatMap { item ->
                    listOf?.definition?.validate(item) ?: emptyList()
                }
            }

            null -> throw error("Type definition must have a base:\n$this")

            else -> emptyList()
        }
    }
}

fun TypeDefinition.resolveTypes(context: ScriptContext): TypeDefinition {

    return when {

        base != null -> {
            resolveByBase(context)
        }

        properties != null -> {
            resolveObject(context)
        }

        listOf != null -> {
            resolveArray(context)
        }

        else -> {
            throw IllegalStateException("Invalid type definition")
        }
    }
}

fun TypeDefinition.resolveByBase(context: ScriptContext): TypeDefinition {

    return when (base) {

        "object" -> {
            resolveObject(context)
        }

        "array" -> {
            resolveArray(context)
        }

        else -> this // No resolution needed for primtive types
    }
}

fun TypeDefinition.resolveObject(context: ScriptContext): TypeDefinition {

    val actualBase = base ?: "object"
    if (actualBase != "object") {
        throw CommandFormatException("With properties defined on a type, base must be 'object', but was: '$base'")
    }

    if (listOf != null) {
        throw CommandFormatException("With properties defined on a type, 'list of' must not be defined")
    }

    val resolvedProperties = properties?.resolveTypes(context) ?: ObjectProperties()

    return TypeDefinition(base = actualBase, properties = resolvedProperties)
}

fun TypeDefinition.resolveArray(context: ScriptContext): TypeDefinition {
    val actualBase = base ?: "array"
    if (actualBase != "array") {
        throw CommandFormatException("With list defined on a type, base must be 'array', but was: '$base'")
    }

    if (properties != null) {
        throw CommandFormatException("With list defined on a type, 'properties' must not be defined")
    }

    val resolvedListOf = listOf?.resolveTypes(context)

    return TypeDefinition(base = actualBase, listOf = TypeReference(resolvedListOf))
}

