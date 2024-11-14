package instacli.util

import com.fasterxml.jackson.databind.JsonNode
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion.VersionFlag
import instacli.language.CommandFormatException
import java.nio.file.Path

object JsonSchemas {

    private val schemas = mutableMapOf<String, JsonSchema?>()

    val factory: JsonSchemaFactory = JsonSchemaFactory.getInstance(VersionFlag.V202012) {
        it.schemaMappers { schemaMappers ->
            schemaMappers.mapPrefix("https://instacli.spec.it/v1/commands", "classpath:commands")
        }
    }

    fun getSchema(schemaName: String): JsonSchema? {
        return schemas.getOrPut(schemaName) {
            loadSchema(schemaName)
        }
    }

    private fun loadSchema(schemaName: String): JsonSchema? {
        val schemaFile = when {
            Resources.exists("commands/$schemaName.yaml") -> "commands/$schemaName.yaml"
            Resources.exists("commands/$schemaName.json") -> "commands/$schemaName.json"
            else -> return null
        }
        return factory.getSchema(Resources.getUri(schemaFile))
    }

    fun load(schemaFile: Path): JsonSchema? {
        return factory.getSchema(schemaFile.toUri())
    }
}


internal fun JsonNode.validateWithSchema(schemaName: String) {
    val schema = JsonSchemas.getSchema(schemaName) ?: return
    val messages = schema.validate(this)
    if (messages.isNotEmpty()) {
        throw CommandFormatException("Schema validation errors according to \"${schemaName}\":\n$messages")
    }
}

