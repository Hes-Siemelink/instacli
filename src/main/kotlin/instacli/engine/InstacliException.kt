package instacli.engine

import com.fasterxml.jackson.databind.JsonNode
import instacli.util.Yaml

open class InstacliException(message: String, var data: JsonNode? = null, cause: Throwable? = null) :
    Exception(message, cause) {

    override val message: String?
        get() = if (data != null) {
            val yaml = Yaml.toString(data).prependIndent("  ")
            "${super.message}\n${yaml}"
        } else {
            super.message
        }
}

class CommandFormatException(message: String, data: JsonNode) : InstacliException(message, data)

class CliScriptException(message: String) : InstacliException(message)

class InstacliInternalException(message: String, data: JsonNode? = null, cause: Throwable) : InstacliException(message, data, cause)