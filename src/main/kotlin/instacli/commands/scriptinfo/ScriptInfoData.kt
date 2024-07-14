package instacli.commands.scriptinfo

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import instacli.language.types.TypeReference

data class ScriptInfoData(
    val description: String? = null,
    val input: TypeReference? = null,
    val hidden: Boolean = false,
    @JsonProperty("instacli-spec")
    val instacliSpec: String? = null
) {

    @JsonCreator
    constructor(textValue: String) : this(description = textValue)
}