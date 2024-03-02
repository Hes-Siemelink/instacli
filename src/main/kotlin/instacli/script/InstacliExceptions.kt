package instacli.script

import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.ErrorData
import instacli.commands.InputData

open class InstacliLanguageException(
    message: String,
    var data: JsonNode? = null,
    cause: Throwable? = null,
    var context: String? = null
) :
    Exception(message, cause) {
}

class CommandFormatException(message: String) : InstacliLanguageException(message)

class CliScriptingException(message: String, data: JsonNode? = null) : InstacliLanguageException(message, data)

class MissingParameterException(message: String, val name: String, val options: InputData) :
    InstacliLanguageException(message)

class InstacliImplementationException(message: String, data: JsonNode? = null, cause: Throwable) :
    InstacliLanguageException(message, data, cause)

class InstacliCommandError(message: String, val data: ErrorData = ErrorData(message)) :
    Exception(message) {

    constructor(message: String, type: String, data: JsonNode? = null) :
            this(
                message, ErrorData(message, type, data)
            )

}
