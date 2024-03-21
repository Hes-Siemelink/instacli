package instacli.util

import com.fasterxml.jackson.databind.JsonNode
import io.kjson.JSONValue

fun JSONValue.toJackson(): JsonNode {
    val json = this.toString()
    return Yaml.parse(json)
}
