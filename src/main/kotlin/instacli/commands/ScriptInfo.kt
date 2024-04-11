package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.fasterxml.jackson.module.kotlin.contains
import instacli.language.*
import instacli.util.Json
import instacli.util.toDomainObject

object ScriptInfo : CommandHandler("Script info", "instacli/script-info"),
    ObjectHandler, ValueHandler, DelayedResolver {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return null
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val scriptInfoData = data.toDomainObject(ScriptInfoData::class)
        val input = scriptInfoData.input ?: return null

        return handleInput(input, context)
    }
}

/**
 * Checks if a variable is set.
 * If not, assigns the default value.
 * Ask for a value if there is no default value and running in interactive mode.
 * Throws exception if there is no default value.
 */
private fun handleInput(data: ObjectNode, context: ScriptContext): ObjectNode {

    val input: ObjectNode = context.variables.getOrPut(INPUT_VARIABLE) { Json.newObject() } as ObjectNode

    // Temporary variables that will hold the contents of the entries so later ones can refer to previous ones
    val variables = context.variables.toMutableMap()

    for ((name, info) in data.fields()) {

        // Already exists
        if (input.contains(name)) {
            continue
        }

        // Resolve variables
        val parameterData = info.resolveVariables(variables).toDomainObject(ParameterData::class)

        // Skip if condition is not valid
        if (!parameterData.conditionValid()) {
            continue
        }

        val answer = when {
            info.has("default") -> info["default"]

            context.interactive -> prompt(parameterData)

            else -> throw MissingParameterException(
                "No value provided for: $name",
                name,
                data.toDomainObject(InputData::class)
            )
        }
        input.set<JsonNode>(name, answer)
        variables[name] = answer
    }

    return input
}

//
// Data model
//

class ScriptInfoData {

    var description: String? = null
    var input: ObjectNode? = null
    var hidden: Boolean = false

    constructor()
    constructor(textValue: String) {
        description = textValue
    }

    fun inputData(): InputData? {
        return input?.toDomainObject(InputData::class)
    }
}