package instacli.commands.userinteraction

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.*
import instacli.language.types.ParameterData
import instacli.util.toDomainObject

/**
 * Asks multiple questions at once
 */
object PromptObject : CommandHandler("Prompt object", "instacli/user-interaction"), ObjectHandler, DelayedResolver {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val answers = data.objectNode()

        // Temporary variables that will hold the contents of the entries so later ones can refer to previous ones
        val variables = context.variables.toMutableMap()

        for ((name, parameterData) in data.fields()) {

            // Resolve variables
            val parameter = parameterData.resolveVariables(variables).toDomainObject(ParameterData::class)

            // Only ask if condition is true
            parameter.conditionValid() || continue

            // Ask user
            val answer = parameter.prompt(name)

            // Add answer to result and to list of variables
            answers.set<JsonNode>(name, answer)
            variables[name] = answer
        }

        return answers
    }
}