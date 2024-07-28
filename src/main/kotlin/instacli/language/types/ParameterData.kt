package instacli.language.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.toCondition

abstract class PropertyDefinition {

    abstract val description: String
    abstract val default: JsonNode?
    abstract val type: TypeReference
    abstract val secret: Boolean
    abstract val enum: List<JsonNode>?
    abstract val select: String
    abstract val displayProperty: String?
    abstract val valueProperty: String?
    abstract val condition: JsonNode?
    abstract val shortOption: String?


    fun conditionValid(): Boolean {
        condition?.let { node ->
            return node.toCondition().isTrue()
        }
        return true
    }
}

data class ParameterData(

    override val description: String = "",
    override val default: JsonNode? = null,
    override val type: TypeReference = TypeReference("string"),
    override val secret: Boolean = false,
    override val enum: List<JsonNode>? = null,
    override val select: String = "single",

    @JsonProperty("display property")
    override val displayProperty: String? = null,

    @JsonProperty("value property")
    override val valueProperty: String? = null,
    override val condition: JsonNode? = null,

    @JsonProperty("short option")
    override val shortOption: String? = null,
) : PropertyDefinition() {

    @JsonCreator
    constructor(textValue: String) : this(description = textValue)
}

