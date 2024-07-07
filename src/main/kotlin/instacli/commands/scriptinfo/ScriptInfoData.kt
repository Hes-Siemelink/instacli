package instacli.commands.scriptinfo

import com.fasterxml.jackson.annotation.JsonCreator
import instacli.language.types.TypeReference

data class ScriptInfoData(
    val description: String? = null,
    val input: TypeReference? = null,
    val hidden: Boolean = false
) {

    @JsonCreator
    constructor(textValue: String) : this(description = textValue)
}