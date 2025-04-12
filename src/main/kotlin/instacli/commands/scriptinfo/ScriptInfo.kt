package instacli.commands.scriptinfo

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.fasterxml.jackson.module.kotlin.contains
import instacli.commands.toCondition
import instacli.commands.userinteraction.prompt
import instacli.language.*
import instacli.language.types.ObjectDefinition
import instacli.language.types.TypeSpecification
import instacli.language.types.resolve
import instacli.util.toDomainObject

object ScriptInfo : CommandHandler("Script info", "instacli/script-info"),
    ObjectHandler, ValueHandler, DelayedResolver {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return null
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val scriptInfoData = data.toDomainObject(ScriptInfoData::class)

        if (scriptInfoData.input != null) {
            handleInput(context, scriptInfoData)
        }

        if (scriptInfoData.inputType != null) {
            handleInputType(context, scriptInfoData.inputType)
        }

        return context.getInputVariables()
    }
}

private fun handleInputType(
    context: ScriptContext,
    inputType: TypeSpecification
) {

    val input = inputType.resolve(context.types).definition

    if (input.properties != null) {
        handleInput(context, input.properties)
    } else {
        // TODO handle array and simple types
    }
}

private fun handleInput(
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

private fun conditionValid(condition: JsonNode?, context: ScriptContext): Boolean {
    condition ?: return true

    return condition.resolveVariables(context.variables).toCondition().isTrue()
}
