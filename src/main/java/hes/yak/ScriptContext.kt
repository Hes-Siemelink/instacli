package hes.yak

import com.fasterxml.jackson.databind.JsonNode
import java.io.File

class ScriptContext {
    val variables = mutableMapOf<String, JsonNode>()
    var scriptLocation: File? = null
    var output: JsonNode? = null
}