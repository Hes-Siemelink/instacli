package instacli.commands.scriptinfo

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.fasterxml.jackson.module.kotlin.contains
import instacli.commands.InputParameters
import instacli.commands.ParameterData
import instacli.commands.userinteraction.prompt
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

private fun handleInput(data: ObjectNode, context: ScriptContext): ObjectNode {

    val input: ObjectNode = context.variables.getOrPut(INPUT_VARIABLE) { Json.newObject() } as ObjectNode

    for ((name, info) in data.fields()) {

        // Already exists
        if (input.contains(name)) {
            continue
        }

        // Resolve variables
        val parameterData = info.resolveVariables(context.variables).toDomainObject(ParameterData::class)

        // Skip if condition is not valid
        if (!parameterData.conditionValid()) {
            continue
        }

        // Find answer
        val answer = when {

            // Get default value
            info.has("default") -> info["default"]

            // Ask user
            context.interactive -> parameterData.prompt()

            else -> throw MissingParameterException(
                "No value provided for: $name",
                name,
                data.toDomainObject(InputParameters::class)
            )
        }
        input.set<JsonNode>(name, answer)
    }

    return input
}
