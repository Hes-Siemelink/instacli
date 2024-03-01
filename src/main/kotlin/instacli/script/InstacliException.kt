package instacli.script

import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.ErrorData
import instacli.commands.InputData

open class InstacliException(
    message: String,
    var data: JsonNode? = null,
    cause: Throwable? = null,
    var context: String? = null
) :
    Exception(message, cause) {
}

class CommandFormatException(message: String) : InstacliException(message)

class CliScriptException(message: String, data: JsonNode? = null) : InstacliException(message, data)

class MissingParameterException(message: String, val name: String, val options: InputData) : InstacliException(message)

class InstacliInternalException(message: String, data: JsonNode? = null, cause: Throwable) :
    InstacliException(message, data, cause)

// TODO Rename this exception and see if it should be merged with 'CliScriptException'
class InstacliErrorCommand(message: String, val data: ErrorData = ErrorData(message)) :
    Exception(message) {
    constructor(data: ErrorData) : this(data.message, data)
}
