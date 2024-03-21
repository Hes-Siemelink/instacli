package instacli.util

import com.fasterxml.jackson.databind.JsonNode
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import instacli.language.CommandFormatException

object JsonSchemas {
    private val schemas = mutableMapOf<String, JsonSchema?>()
    private val factory by lazy {
        JsonSchemaFactory.builder(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012))
            .objectMapper(Yaml.mapper).build()
    }

    fun getSchema(name: String): JsonSchema? {
        return schemas.getOrPut(name) {
            loadSchema("schema/$name.schema.json")
        }
    }

    private fun loadSchema(schemaFile: String): JsonSchema? {
        if (!Resources.exists(schemaFile)) return null
        return factory.getSchema(Resources.getUri(schemaFile))
    }
}


internal fun JsonNode.validateWithSchema(name: String) {
    val schema = JsonSchemas.getSchema(name) ?: return
    val messages = schema.validate(this)
    if (messages.isNotEmpty()) {
        throw CommandFormatException("Schema validation errors:\n$messages")
    }
}
