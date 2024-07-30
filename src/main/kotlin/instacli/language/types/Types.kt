package instacli.language.types

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode

interface TypeRegistry {
    val types: Map<String, Type>
}

interface Type {
    fun validate(data: JsonNode): List<String>
}

object BuiltinTypes : TypeRegistry {
    override val types = mapOf(
        "string" to STRING_TYPE,
        "boolean" to BOOLEAN_TYPE,
        "object" to OBJECT_TYPE,
        "array" to ARRAY_TYPE
    )
}

//
// Types
//

private val ARRAY_TYPE = TypeDefinition(base = "array")
private val OBJECT_TYPE = TypeDefinition(base = "object")
private val STRING_TYPE = TypeDefinition(base = "string")
private val BOOLEAN_TYPE = TypeDefinition(base = "boolean")

private val OK = listOf<String>()

object StringType : Type {
    override fun validate(data: JsonNode): List<String> {
        if (data !is TextNode) {
            return listOf("Data should be string but is ${data::class.simpleName}")
        }
        return OK
    }
}

object BooleanType : Type {
    override fun validate(data: JsonNode): List<String> {
        if (data !is BooleanNode) {
            return listOf("Data should be boolean but is ${data::class.simpleName}")
        }
        return OK
    }
}

object ObjectType : Type {
    override fun validate(data: JsonNode): List<String> {
        if (data !is ObjectNode) {
            return listOf("Data should be object but is ${data::class.simpleName}")
        }
        return OK
    }
}


object ArrayType : Type {
    override fun validate(data: JsonNode): List<String> {
        if (data !is ArrayNode) {
            return listOf("Data should be array but is ${data::class.simpleName}")
        }
        return OK
    }
}


