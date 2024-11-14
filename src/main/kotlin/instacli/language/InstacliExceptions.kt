package instacli.language

import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.errors.ErrorData
import instacli.language.types.ObjectDefinition

open class InstacliLanguageException(
    message: String,
    var data: JsonNode? = null,
    cause: Throwable? = null,
    var context: String? = null
) :
    Exception(message, cause)

class CommandFormatException(message: String) : InstacliLanguageException(message)

class CliScriptingException(message: String, data: JsonNode? = null) : InstacliLanguageException(message, data)

class MissingParameterException(
    message: String,
    val name: String,
    val parameters: ObjectDefinition
) :
    InstacliLanguageException(message)

class InstacliImplementationException(message: String, data: JsonNode? = null, cause: Throwable? = null) :
    InstacliLanguageException(message, data, cause)

open class InstacliCommandError(message: String, val error: ErrorData = ErrorData(message = message)) :
    Exception(message) {

    constructor(error: ErrorData) :
            this(error.message, error)

    constructor(type: String, message: String, data: JsonNode? = null) :
            this(ErrorData(type, message, data))
}
