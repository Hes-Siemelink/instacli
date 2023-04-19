package yay.core

import com.fasterxml.jackson.databind.JsonNode

class ScriptException(message: String, var data: JsonNode? = null, cause: Throwable? = null) :
    Exception(message, cause) {

    override val message: String?
        get() = if (data != null) {
            "${super.message}\n${Yaml.toString(data)}"
        } else {
            super.message
        }
}