package instacli.util

import com.fasterxml.jackson.databind.JsonNode
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import instacli.script.CommandFormatException
import java.net.URI

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
        val resource = getResource(schemaFile) ?: return null
        return factory.getSchema(resource)
    }
}

private fun getResource(classpathResource: String): URI? {
    return object {}.javaClass.getResource("/$classpathResource")?.toURI()
}

internal fun JsonNode.validateWithSchema(name: String) {
    val schema = JsonSchemas.getSchema(name) ?: return
    val messages = schema.validate(this)
    if (messages.isNotEmpty()) {
        throw CommandFormatException("Schema validation errors:\n$messages")
    }
}
