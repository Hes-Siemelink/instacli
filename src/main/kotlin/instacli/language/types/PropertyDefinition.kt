package instacli.language.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.toCondition

abstract class PropertyDefinition {

    abstract val description: String?
    abstract val optional: Boolean
    abstract val default: JsonNode?
    abstract val type: TypeSpecification?
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

/**
 * Used in type definitions to define properties
 */
data class PropertySpecification(

    override val description: String? = null,
    override val optional: Boolean = false,
    override val default: JsonNode? = null,
    override val type: TypeSpecification? = null,
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
    constructor(textValue: String) : this(type = TypeSpecification(textValue)) // Defaults to type name reference

    fun withType(type: TypeSpecification?): PropertySpecification {
        return PropertySpecification(
            description = description,
            optional = optional,
            default = default,
            type = type,
            secret = secret,
            enum = enum,
            select = select,
            displayProperty = displayProperty,
            valueProperty = valueProperty,
            condition = condition,
            shortOption = shortOption
        )
    }
}

/**
 * Used in Prompt and ScriptInfo to define parameters
 */
data class ParameterData(

    override val description: String? = null,
    override val optional: Boolean = false,
    override val default: JsonNode? = null,
    override val type: TypeSpecification? = null,
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
    constructor(textValue: String) : this(description = textValue)  // Defaults to description
}
