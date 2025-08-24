package instacli.commands.scriptinfo

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.contains
import instacli.commands.toCondition
import instacli.commands.userinteraction.prompt
import instacli.language.*
import instacli.language.types.ObjectDefinition
import instacli.language.types.ParameterData
import instacli.util.toDomainObject

object InputParameters : CommandHandler("Input parameters", "instacli/script-info"),
    ObjectHandler, DelayedResolver {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode {
        val inputData = data.toDomainObject(InputParameterData::class)

        handleInput(context, inputData)

        return context.getInputVariables()
    }

    fun handleInput(
        context: ScriptContext,
        input: ObjectDefinition
    ) {

        for ((name, info) in input.properties.entries) {

            // Already exists
            if (context.getInputVariables().contains(name)) {
                // Copy variable to top level
                context.variables[name] = context.getInputVariables()[name]
                continue
            }

            // Skip if condition is not valid
            if (!conditionValid(info.condition, context)) {
                continue
            }

            // Find answer
            val answer: JsonNode = when {

                // Get default value
                info.default != null -> info.default!!

                // Ask user
                context.interactive -> info.prompt(name)

                else -> throw MissingParameterException(
                    "No value provided for: $name",
                    name,
                    input
                )
            }

            context.getInputVariables().set<JsonNode>(name, answer)
            context.variables[name] = answer
        }
    }
}

private fun conditionValid(condition: JsonNode?, context: ScriptContext): Boolean {
    condition ?: return true

    return condition.resolveVariables(context.variables).toCondition().isTrue()
}

data class InputParameterData(
    @get:JsonAnyGetter
    override val properties: Map<String, ParameterData> = mutableMapOf()
) : ObjectDefinition {
}

