package instacli.util

import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion

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

