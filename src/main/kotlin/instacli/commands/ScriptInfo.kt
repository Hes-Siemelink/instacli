package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.fasterxml.jackson.module.kotlin.contains
import instacli.language.*
import instacli.util.Json
import instacli.util.toDomainObject

object ScriptInfo : CommandHandler("Script info"), ObjectHandler, ValueHandler, DelayedResolver {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return null
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val scriptInfoData = data.toDomainObject(ScriptInfoData::class)
        val input = scriptInfoData.input ?: return null

        return handleInput(context, input)
    }
}

/**
 * Checks if a variable is set.
 * If not, assigns the default value.
 * Ask for a value if there is no default value and running in interactive mode.
 * Throws exception if there is no default value.
 */
private fun handleInput(
    context: ScriptContext,
    inputData: InputData
): ObjectNode {
    val input: ObjectNode = context.variables.getOrPut(INPUT_VARIABLE) { Json.newObject() } as ObjectNode
    for ((name, info) in inputData.parameters) when {

        // Already exists
        input.contains(name) -> {
            continue
        }

        // Use default value
        info.default != null -> {
            input.set<JsonNode>(name, info.default as JsonNode)
        }

        // Ask user
        context.interactive -> {
            val answer = prompt(info)
            input.set<JsonNode>(name, answer)
        }

        else -> {
            throw MissingParameterException("No value provided for: $name", name, inputData)
        }
    }

    return input
}

//
// Data model
//

class ScriptInfoData {

    var description: String? = null
    var input: InputData? = null
    var hidden: Boolean = false

    constructor()
    constructor(textValue: String) {
        description = textValue
    }
}