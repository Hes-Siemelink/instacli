package instacli.script

import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.InputData
import instacli.util.toDisplayString

open class InstacliException(
    message: String,
    var data: JsonNode? = null,
    cause: Throwable? = null,
    var context: String? = null
) :
    Exception(message, cause) {

    val details: String?
        get() = data?.let {
            val yaml = data.toDisplayString().prependIndent("  ")
            "In command:\n\n${yaml}".trimMargin()
        }
}

class CommandFormatException(message: String) : InstacliException(message)

class CliScriptException(message: String) : InstacliException(message)

class MissingParameterException(message: String, val name: String, val options: InputData) : InstacliException(message)

class InstacliInternalException(message: String, data: JsonNode? = null, cause: Throwable) :
    InstacliException(message, data, cause)