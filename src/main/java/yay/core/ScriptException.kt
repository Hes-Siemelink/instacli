package yay.core

import com.fasterxml.jackson.databind.JsonNode

class ScriptException(message: String, val data: JsonNode? = null) : Exception(message) {
    override val message: String?
        get() = if (data != null) {
            "${super.message}\n${Yaml.toString(data)}"
        } else {
            super.message
        }
}