package instacli.language.types

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import instacli.language.InstacliCommandError

interface TypeRegistry {
    val types: Map<String, Type>
}

abstract class Type(val name: String) {
    abstract fun validate(data: JsonNode): List<String>
}

object BuiltinTypes : TypeRegistry {
    override val types: Map<String, Type>
        get() = _types

    private val _types = typeMap(
        StringType,
        ObjectType,
        ArrayType
    )

    private fun typeMap(vararg types: Type): Map<String, Type> {
        return types.associateBy { it.name }
    }
}

//
// Types
//

private val OK = listOf<String>()

object StringType : Type("string") {
    override fun validate(data: JsonNode): List<String> {
        if (data !is TextNode) {
            return listOf("Data should be string but is ${data::class.simpleName}")
        }
        return OK
    }
}

object ObjectType : Type("object") {
    override fun validate(data: JsonNode): List<String> {
        if (data !is ObjectNode) {
            return listOf("Data should be object but is ${data::class.simpleName}")
        }
        return OK
    }
}

object ArrayType : Type("array") {
    override fun validate(data: JsonNode): List<String> {
        if (data !is ArrayNode) {
            return listOf("Data should be array but is ${data::class.simpleName}")
        }
        return OK
    }
}


data class ObjectProperties(
    @get:JsonAnyGetter
    val properties: Map<String, JsonNode> = mutableMapOf()
) : Type("object") {

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
