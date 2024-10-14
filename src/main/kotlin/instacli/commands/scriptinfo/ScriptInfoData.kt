package instacli.commands.scriptinfo

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import instacli.language.types.ObjectDefinition
import instacli.language.types.ParameterData
import instacli.language.types.PropertyDefinition
import instacli.language.types.TypeSpecification

data class ScriptInfoData(
    val description: String? = null,
    val input: Map<String, ParameterData>? = null,
    @JsonProperty("input type")
    val inputType: TypeSpecification? = null,
    val hidden: Boolean = false,
    @JsonProperty("instacli-spec")
    val instacliSpec: String? = null
) : ObjectDefinition {

    @JsonCreator
    constructor(textValue: String) : this(description = textValue)

    override val properties: Map<String, PropertyDefinition>
        get() = input ?: inputType?.properties?.properties ?: emptyMap()
}