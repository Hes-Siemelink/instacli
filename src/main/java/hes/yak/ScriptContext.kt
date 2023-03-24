package hes.yak

import com.fasterxml.jackson.databind.JsonNode

class ScriptContext() {
    val variables = mutableMapOf<String, JsonNode>()
}