package instacli.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.nio.file.Path

object Yaml {

    private val factory = YAMLFactory()
        .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
        .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)

    val mapper = ObjectMapper(factory).registerKotlinModule()

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

fun JsonNode?.toDisplayYaml(): String {
    this ?: return ""
    if (isTextual) {
        return textValue()
    }
    return Yaml.mapper.writeValueAsString(this).trim()
}
