package instacli.commands

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

class ParameterData {

    var description: String = ""
    var default: JsonNode? = null
    var type: String = ""
    var enum: List<JsonNode>? = null
    var select: String = "single" // TODO use enum

    @JsonProperty("display property")
    var displayProperty: String? = null

    @JsonProperty("value property")
    var valueProperty: String? = null
    var condition: JsonNode? = null

    @JsonProperty("short option")
    var shortOption: String? = null

    constructor()
    constructor(textValue: String) {
        description = textValue
    }

    fun conditionValid(): Boolean {
        condition?.let { node ->
            return node.toCondition().isTrue()
        }
        return true
    }
}