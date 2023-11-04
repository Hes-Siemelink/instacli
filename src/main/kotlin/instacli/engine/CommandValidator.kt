package instacli.engine

import com.fasterxml.jackson.databind.JsonNode
import com.networknt.schema.JsonSchema
import instacli.util.Yaml

private val schemas = mutableMapOf<String, JsonSchema?>()

fun CommandHandler.validate(data: JsonNode) {
    val schema = getSchema(name) ?: return

    val messages = schema.validate(data)
    if (messages.isNotEmpty()) {
        throw CommandFormatException("Schema validation errors:\n$messages", data)
    }
}

fun getSchema(name: String): JsonSchema? {
    return schemas.getOrPut(name) {
        Yaml.getSchema("schema/$name.schema.json")
    }
}
