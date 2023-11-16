package instacli.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import java.io.File
import java.io.InputStream
import java.net.URI

object Yaml {

    private val factory = YAMLFactory()
        .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
        .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)

    val mapper = ObjectMapper(factory)
    // Not using ObjectMapper(factory).registerKotlinModule() because it trips up Graal native image

    init {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    fun readFile(source: File): JsonNode? {
        return mapper.readValue(source, JsonNode::class.java)
    }

    fun readResource(classpathResource: String): JsonNode {
        val stream = getResourceAsStream(classpathResource)

        return mapper.readTree(stream)
    }

    fun parse(source: File): List<JsonNode> {
        val yamlParser = factory.createParser(source)
        return mapper
            .readValues(yamlParser, JsonNode::class.java)
            .readAll()
    }

    fun parse(source: String): JsonNode {
        return mapper.readValue(source, JsonNode::class.java)
    }

    fun toString(node: JsonNode?): String {
        node ?: return ""
        if (node.isTextual) {
            return node.textValue()
        }
        return mapper.writeValueAsString(node).trim()
    }

    fun mutableMapOf(data: JsonNode): MutableMap<String, JsonNode> {
        val map = mutableMapOf<String, JsonNode>()
        for (field in data.fields()) {
            map[field.key] = field.value
        }
        return map
    }

}

fun objectNode(): ObjectNode {
    return ObjectNode(JsonNodeFactory.instance)
}

/**
 * Factory method for JSON objects with single field.
 */
fun objectNode(key: String, value: String): ObjectNode {
    return ObjectNode(JsonNodeFactory.instance).put(key, value)
}

fun getResourceAsStream(classpathResource: String): InputStream? {
    return object {}.javaClass.getResourceAsStream("/$classpathResource")
}

fun getResource(classpathResource: String): URI? {
    return object {}.javaClass.getResource("/$classpathResource")?.toURI()
}

fun JsonNode.toDisplayString(): String {
    if (isTextual) {
        return textValue()
    }
    return Yaml.mapper.writeValueAsString(this).trim()
}
