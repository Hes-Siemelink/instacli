package instacli.language.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.toCondition
import instacli.language.ScriptContext

abstract class PropertyDefinition {

    abstract val description: String?
    abstract val default: JsonNode?
    abstract val type: TypeReference?
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
data class PropertyData(

    override val description: String? = null,
    override val default: JsonNode? = null,
    override val type: TypeReference? = null,
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
    constructor(textValue: String) : this(type = TypeReference(textValue)) // Defaults to type name reference
}

/**
 * Used in Prompt and ScriptInfo to define parameters
 */
data class ParameterData(

    override val description: String? = null,
    override val default: JsonNode? = null,
    override val type: TypeReference? = null,
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


fun PropertyData.resolveTypes(context: ScriptContext): PropertyData {

    val actualType = type?.resolveTypes(context)?.toTypeReference()

    return PropertyData(
        description = description,
        default = default,
        type = actualType,
        secret = secret,
        enum = enum,
        select = select,
        displayProperty = displayProperty,
        valueProperty = valueProperty,
        condition = condition,
        shortOption = shortOption
    )
}
