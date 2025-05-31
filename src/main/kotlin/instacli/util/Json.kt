package instacli.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.*
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.serialization.json.*
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

//
// Kotlinx.serialization conversion
//

fun ObjectNode.toKotlinx(): JsonObject {
    return buildJsonObject {
        fields().forEach { (key, value) ->
            put(key, value.toKotlinx())
        }
    }
}

private fun ArrayNode.toKotlinx(): JsonArray {
    return buildJsonArray {
        elements().forEach {
            add(it.toKotlinx())
        }
    }
}

private fun JsonNode.toKotlinx(): JsonElement {
    return when (this) {
        is NullNode, is MissingNode -> JsonNull
        is BooleanNode -> JsonPrimitive(booleanValue())
        is NumericNode -> JsonPrimitive(numberValue())
        is TextNode -> JsonPrimitive(textValue())
        is ArrayNode -> this.toKotlinx()
        is ObjectNode -> this.toKotlinx()
        else -> throw IllegalArgumentException("Unknown JsonNode type: ${this.javaClass}")
    }
}

//
// Kotlinx.serialization => Jackson conversion
//


fun JsonObject.toJackson(nodeFactory: JsonNodeFactory = JsonNodeFactory.instance): ObjectNode {
    val jacksonObject = nodeFactory.objectNode()
    this.entries.forEach { (key, value) ->
        jacksonObject.set<JsonNode>(key, value.toJackson(nodeFactory))
    }
    return jacksonObject
}

private fun JsonArray.toJackson(nodeFactory: JsonNodeFactory): ArrayNode {
    val jacksonArray = nodeFactory.arrayNode(this.size)
    this.forEach { element ->
        jacksonArray.add(element.toJackson(nodeFactory))
    }
    return jacksonArray
}

fun JsonElement.toJackson(nodeFactory: JsonNodeFactory = JsonNodeFactory.instance): JsonNode {
    return when (this) {
        is JsonNull -> nodeFactory.nullNode()
        is JsonObject -> this.toJackson(nodeFactory)
        is JsonArray -> this.toJackson(nodeFactory)
        is JsonPrimitive -> this.toJackson(nodeFactory)
    }
}

private fun JsonPrimitive.toJackson(nodeFactory: JsonNodeFactory): JsonNode {
    if (this.isString) {
        return nodeFactory.textNode(this.content)
    }
    // Try to be as specific as possible with number types
    this.longOrNull?.let { return nodeFactory.numberNode(it) }
    this.doubleOrNull?.let { return nodeFactory.numberNode(it) }
    this.booleanOrNull?.let { return nodeFactory.booleanNode(it) }

    // Fallback for other potential primitive types
    return nodeFactory.textNode(this.content)
}