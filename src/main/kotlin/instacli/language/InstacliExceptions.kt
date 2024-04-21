package instacli.language

import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.InputParameters
import instacli.commands.errors.ErrorData

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

class MissingParameterException(message: String, val name: String, val options: InputParameters) :
    InstacliLanguageException(message)

class InstacliImplementationException(message: String, data: JsonNode? = null, cause: Throwable) :
    InstacliLanguageException(message, data, cause)

open class InstacliCommandError(message: String, val error: ErrorData = ErrorData(message)) :
    Exception(message) {

    constructor(message: String, type: String, data: JsonNode? = null) :
            this(
                message, ErrorData(message, type, data)
            )

}
