package instacli.commands.userinteraction

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.commands.ParameterData
import instacli.language.*
import instacli.util.toDomainObject

/**
 * Asks multiple questions at once
 */
object PromptObject : CommandHandler("Prompt object", "instacli/user-interaction"), ObjectHandler, DelayedResolver {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        // Temporary variables that will hold the contents of the entries so later ones can refer to previous ones
        val variables = context.variables.toMutableMap()

        val answers = data.objectNode()
        for ((field, rawParameter) in data.fields()) {

            // Resolve variables
            val parameterData = rawParameter.resolveVariables(variables).toDomainObject(ParameterData::class)

            // Only ask if condition is true
            parameterData.conditionValid() || continue

            // Ask user
            val answer = parameterData.prompt()

            // Add answer to result and to list of variables
            answers.set<JsonNode>(field, answer)
            variables[field] = answer
        }

        return answers
    }
}