package instacli.language.types

import com.fasterxml.jackson.databind.JsonNode

data class TypeDefinition(
    val base: String? = "object",
    val properties: ObjectProperties
) : Type {

    override fun validate(data: JsonNode): List<String> {
        return this@TypeDefinition.properties.validate(data)
    }
}