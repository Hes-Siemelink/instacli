package instacli.commands.errors

import com.fasterxml.jackson.databind.JsonNode

data class ErrorData(
    var message: String = "An error occurred",
    var type: String = "general",
    var data: JsonNode? = null
)
