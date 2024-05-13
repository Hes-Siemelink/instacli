package instacli.language.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.toCondition

data class ParameterData(

    val description: String = "",
    val default: JsonNode? = null,
    val type: String = "",
    val secret: Boolean = false,
    val enum: List<JsonNode>? = null,
    val select: String = "single", // TODO use enum

    @JsonProperty("display property")
    val displayProperty: String? = null,

    @JsonProperty("value property")
    val valueProperty: String? = null,
    val condition: JsonNode? = null,

    @JsonProperty("short option")
    val shortOption: String? = null,
) {

    @JsonCreator
    constructor(textValue: String) : this(description = textValue)

    fun conditionValid(): Boolean {
        condition?.let { node ->
            return node.toCondition().isTrue()
        }
        return true
    }
}