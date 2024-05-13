package instacli.commands.scriptinfo

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.types.InputParameters
import instacli.util.toDomainObject

data class ScriptInfoData(
    val description: String? = null,
    val input: ObjectNode? = null,
    val hidden: Boolean = false
) {

    @JsonCreator
    constructor(textValue: String) : this(description = textValue)

    fun inputData(): InputParameters? {
        return input?.toDomainObject(InputParameters::class)
    }
}