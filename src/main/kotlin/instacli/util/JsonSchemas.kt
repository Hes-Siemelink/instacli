package instacli.util

import com.fasterxml.jackson.databind.JsonNode
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion.VersionFlag
import instacli.language.CommandFormatException
import java.nio.file.Path


object JsonSchemas {
    private val schemas = mutableMapOf<String, JsonSchema?>()
    val factory = JsonSchemaFactory.getInstance(VersionFlag.V202012)
    

    fun getSchema(name: String): JsonSchema? {
        return schemas.getOrPut(name) {
            loadSchema(name)
        }
    }

    private fun loadSchema(name: String): JsonSchema? {
        val schemaFile = when {
            Resources.exists("schema/$name.schema.yaml") -> "schema/$name.schema.yaml"
            Resources.exists("schema/$name.schema.json") -> "schema/$name.schema.json"
            else -> return null
        }
        return factory.getSchema(Resources.getUri(schemaFile))
    }

    fun load(schemaFile: Path): JsonSchema? {
        return factory.getSchema(schemaFile.toUri())
    }
}


internal fun JsonNode.validateWithSchema(name: String) {
    val schema = JsonSchemas.getSchema(name) ?: return
    val messages = schema.validate(this)
    if (messages.isNotEmpty()) {
        val schemaName = schema.toString()

        throw CommandFormatException("Schema validation errors according to \"${schemaName}\":\n$messages")
    }
}

