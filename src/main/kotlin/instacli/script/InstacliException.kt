package instacli.script

import com.fasterxml.jackson.databind.JsonNode
import instacli.util.Yaml

open class InstacliException(message: String, var data: JsonNode? = null, cause: Throwable? = null, var context: String? = null) :
    Exception(message, cause) {

    val details: String?
        get() = data?.let {
            val yaml = Yaml.toString(data).prependIndent("  ")
            "In command:\n\n${yaml}".trimMargin()
        }
}

class CommandFormatException(message: String) : InstacliException(message)

class CliScriptException(message: String) : InstacliException(message)

class InstacliInternalException(message: String, data: JsonNode? = null, cause: Throwable) : InstacliException(message, data, cause)