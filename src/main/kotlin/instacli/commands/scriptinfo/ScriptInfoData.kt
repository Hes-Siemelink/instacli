package instacli.commands.scriptinfo

import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.commands.InputParameters
import instacli.util.toDomainObject

class ScriptInfoData {

    var description: String? = null
    var input: ObjectNode? = null
    var hidden: Boolean = false

    constructor()
    constructor(textValue: String) {
        description = textValue
    }

    fun inputData(): InputParameters? {
        return input?.toDomainObject(InputParameters::class)
    }
}