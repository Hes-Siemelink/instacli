package instacli.commands.scriptinfo

import com.fasterxml.jackson.annotation.JsonCreator
import instacli.language.types.TypeDefinition

data class ScriptInfoData(
    val description: String? = null,
    val input: TypeDefinition? = null,
    val hidden: Boolean = false
) {

    @JsonCreator
    constructor(textValue: String) : this(description = textValue)

//    fun inputData(): TypeDefinition? {
//        return input?.toDomainObject(InputParameters::class)
//    }
}