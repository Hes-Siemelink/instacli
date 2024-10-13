package instacli.language.types

import com.fasterxml.jackson.databind.JsonNode

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


