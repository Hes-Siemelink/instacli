package instacli.language.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode

data class TypeSpecification(
    val name: String? = null,
    val base: String? = null,
    val properties: ObjectProperties? = null,
    @JsonProperty("list of")
    val listOf: TypeSpecification? = null,
) {
    @JsonCreator
    constructor(typeName: String) : this(name = typeName)
}

fun TypeSpecification.validate(data: JsonNode): List<String> {

    return when (base) {

        Type.STRING -> {
            if (data !is TextNode) {
                return listOf("Data should be string but is ${data::class.simpleName}")
            }
            emptyList()
        }

        Type.BOOLEAN -> {
            if (data !is BooleanNode) {
                return listOf("Data should be boolean but is ${data::class.simpleName}")
            }
            emptyList()
        }

        Type.OBJECT -> {
            if (data !is ObjectNode) {
                return listOf("Data should be object but is ${data::class.simpleName}")
            }
            properties?.validate(data) ?: emptyList()
        }

        Type.ARRAY -> {
            if (data !is ArrayNode) {
                return listOf("Data should be array but is ${data::class.simpleName}")
            }
            data.flatMap { item ->
                listOf?.validate(item) ?: emptyList()
            }
        }

        null -> throw error("Type definition must have a base:\n$this")

        else -> emptyList()
    }
}
