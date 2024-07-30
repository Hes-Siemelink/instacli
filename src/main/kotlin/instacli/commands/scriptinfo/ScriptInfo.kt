package instacli.commands.scriptinfo

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.fasterxml.jackson.module.kotlin.contains
import instacli.commands.toCondition
import instacli.commands.userinteraction.prompt
import instacli.language.*
import instacli.language.types.ObjectDefinition
import instacli.language.types.TypeDefinition
import instacli.util.Json
import instacli.util.toDomainObject

object ScriptInfo : CommandHandler("Script info", "instacli/script-info"),
    ObjectHandler, ValueHandler, DelayedResolver {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return null
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val scriptInfoData = data.toDomainObject(ScriptInfoData::class)
        val providedInput: ObjectNode = context.variables.getOrPut(INPUT_VARIABLE) { Json.newObject() } as ObjectNode

        return when {
            scriptInfoData.input != null -> {
                handleInput(providedInput, scriptInfoData, context)
            }

            scriptInfoData.inputType != null -> {
                val input = scriptInfoData.inputType.name?.let {
                    context.getType(it) as TypeDefinition? ?: throw CliScriptingException("Type not found: $it")
                }
                    ?: scriptInfoData.inputType.definition
                    ?: throw CommandFormatException("Missing type definition on Script info input ")

                handleInput(providedInput, input.properties, context)
            }

            else -> {
                providedInput
            }
        }
    }
}

private fun handleInput(
    providedInput: ObjectNode,
    input: ObjectDefinition,
    context: ScriptContext
): ObjectNode {

    for ((name, info) in input.properties.entries) {

        // Already exists
        if (providedInput.contains(name)) {
            continue
        }

        // Skip if condition is not valid
        if (!conditionValid(info.condition, context)) {
            continue
        }

        // Find answer
        val answer = when {

            // Get default value
            info.default != null -> info.default

            // Ask user
            context.interactive -> info.prompt()

            else -> throw MissingParameterException(
                "No value provided for: $name",
                name,
                input
            )
        }
        providedInput.set<JsonNode>(name, answer)
    }

    return providedInput
}

private fun conditionValid(condition: JsonNode?, context: ScriptContext): Boolean {
    condition ?: return true

    return condition.resolveVariables(context.variables).toCondition().isTrue()
}
