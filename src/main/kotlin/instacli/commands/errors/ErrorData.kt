package instacli.commands.errors

import com.fasterxml.jackson.databind.JsonNode

class ErrorData {

    var message: String = "An error occurred"
    var type: String = "general"
    var data: JsonNode? = null

    constructor()
    constructor(message: String = "An error occurred", type: String = "general", data: JsonNode? = null) {
        this.message = message
        this.type = type
        this.data = data
    }
}
