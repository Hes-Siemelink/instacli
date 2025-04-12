package instacli.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlin.reflect.KClass

object Json {

    val mapper: ObjectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).registerKotlinModule()
    val compactMapper: ObjectMapper = ObjectMapper().registerKotlinModule()

    fun newArray(): ArrayNode {
        return ArrayNode(JsonNodeFactory.instance)
    }

    fun newObject(): ObjectNode {
        return ObjectNode(JsonNodeFactory.instance)
    }

    /**
     * Factory method for JSON objects with single field.
     */
    fun newObject(key: String, value: String): ObjectNode {
        return ObjectNode(JsonNodeFactory.instance).put(key, value)
    }

    fun newObject(map: Map<String, String>): ObjectNode {
        return mapper.valueToTree(map)
    }
}


fun List<JsonNode>.toJson(): ArrayNode {
    return ArrayNode(JsonNodeFactory.instance, this)
}

fun JsonNode.asArray(): ArrayNode {
    return when (this) {
        is ArrayNode -> this
        else -> ArrayNode(JsonNodeFactory.instance).add(this)
    }
}

fun ObjectNode.add(vars: Map<String, String>) {
    for (variable in vars) {
        this.set<JsonNode>(variable.key, TextNode(variable.value))
    }
}

fun <T : Any> JsonNode.toDomainObject(dataClass: KClass<T>): T {
    return Json.mapper.treeToValue(this, dataClass.java)
}

fun <T : Any> T.updateWith(content: JsonNode): T {
    return Json.mapper.readerForUpdating(this).readValue(content)
}

fun JsonNode?.toDisplayJson(): String {
    this ?: return ""
    return Json.mapper.writeValueAsString(this).trim()
}

fun JsonNode?.toCompactJson(): String {
    this ?: return ""
    return Json.compactMapper.writeValueAsString(this).trim()
}

abstract class JsonProcessor {

    fun process(node: JsonNode): JsonNode {
        return when (node) {
            is ArrayNode -> processArray(node)
            is ObjectNode -> processObject(node)
            is TextNode -> processText(node)
            else -> processOther(node)
        }
    }

    open fun processArray(node: ArrayNode): JsonNode {

        for (item in node.withIndex()) {
            node.set(item.index, process(item.value))
        }

        return node
    }

    open fun processObject(node: ObjectNode): JsonNode {

        for (field in node.fields()) {
            node.set<JsonNode>(field.key, process(field.value))
        }

        return node
    }

    open fun processText(node: TextNode): JsonNode {
        return node
    }

    open fun processOther(node: JsonNode): JsonNode {
        return node
    }
}
