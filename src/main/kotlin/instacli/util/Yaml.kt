package instacli.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import java.nio.file.Path

object Yaml {

    private val factory = YAMLFactory()
        .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
        .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)

    val mapper = ObjectMapper(factory)
    val jsonMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

    init {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    fun readFile(source: Path): JsonNode {
        return mapper.readValue(source.toFile(), JsonNode::class.java)
    }

    fun readResource(classpathResource: String): JsonNode {
        val stream = Resources.stream(classpathResource)

        return mapper.readTree(stream)
    }

    fun parse(source: Path): List<JsonNode> {
        val yamlParser = factory.createParser(source.toFile())
        return mapper
            .readValues(yamlParser, JsonNode::class.java)
            .readAll()
    }

    fun parseAsFile(content: String): List<JsonNode> {
        val yamlParser = factory.createParser(content)
        return mapper
            .readValues(yamlParser, JsonNode::class.java)
            .readAll()
    }

    fun parse(source: String): JsonNode {
        return mapper.readValue(source, JsonNode::class.java)
    }

    inline fun <reified T> parse(node: JsonNode): T {
        return mapper.treeToValue(node, T::class.java)
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

fun objectNode(map: Map<String, String>): ObjectNode {
    return Yaml.mapper.valueToTree(map)
}

fun JsonNode?.toDisplayString(): String {
    this ?: return ""
    if (isTextual) {
        return textValue()
    }
    return Yaml.mapper.writeValueAsString(this).trim()
}

fun JsonNode?.toJsonString(): String {
    this ?: return ""
    return Yaml.jsonMapper.writeValueAsString(this).trim()
}

fun List<JsonNode>.toArrayNode(): ArrayNode {
    return ArrayNode(JsonNodeFactory.instance, this)
}