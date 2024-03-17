package instacli.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlin.reflect.KClass

object Json {
    val mapper: ObjectMapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

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

fun <T : Any> JsonNode.toDomainObject(dataClass: KClass<T>): T {
    return Json.mapper.treeToValue(this, dataClass.java)
}

fun JsonNode?.toDisplayJson(): String {
    this ?: return ""
    return Json.mapper.writeValueAsString(this).trim()
}
