package instacli.commands.errors

import com.fasterxml.jackson.databind.JsonNode

data class ErrorData(
    var type: String = "general",
    var message: String = "An error occurred",
    var data: JsonNode? = null
)
