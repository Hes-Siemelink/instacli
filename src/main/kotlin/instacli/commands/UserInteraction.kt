package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.github.kinquirer.core.Choice
import instacli.language.*
import instacli.util.UserPrompt
import instacli.util.toDisplayYaml
import instacli.util.toDomainObject

/**
 * Asks user through simple text prompt
 */
object Prompt : CommandHandler("Prompt", "instacli/user-interaction"), ValueHandler, ObjectHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode {
        return UserPrompt.prompt(data.textValue())
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val parameterData = data.toDomainObject(ParameterData::class)

        // Only ask if condition is true
        parameterData.conditionValid() || return null

        return prompt(parameterData)
    }
}


fun prompt(parameter: ParameterData): JsonNode {
    return when {
        parameter.enum != null && parameter.select == "single" -> promptChoice(parameter)
        parameter.enum != null && parameter.select == "multiple" -> promptChoice(parameter, true)
        parameter.type == "password" -> promptText(parameter, password = true)
        parameter.type == "boolean" -> promptBoolean(parameter)
        else -> promptText(parameter)
    }
}

private fun promptText(parameter: ParameterData, password: Boolean = false): JsonNode {
    return UserPrompt.prompt(parameter.description, parameter.default?.asText() ?: "", password)
}

private fun promptBoolean(parameter: ParameterData, password: Boolean = false): JsonNode {
    val answer = UserPrompt.prompt(parameter.description, parameter.default?.asText() ?: "", password)
    return if (answer.textValue() == "true") BooleanNode.TRUE else BooleanNode.FALSE
}

private fun promptChoice(parameter: ParameterData, multiple: Boolean = false): JsonNode {

    val choices = parameter.enum?.map {
        if (parameter.displayProperty == null) {
            Choice(it.toDisplayYaml(), it)
        } else {
            Choice(it[parameter.displayProperty].textValue(), it)
        }
    } ?: emptyList()

    val answer = UserPrompt.select(parameter.description, choices, multiple)

    return onlyWithField(answer, parameter.valueProperty)
}

private fun onlyWithField(node: JsonNode, field: String?): JsonNode {
    if (field == null) {
        return node
    }

    return when (node) {
        is ObjectNode -> node[field]
        is ArrayNode -> {
            val copy = node.arrayNode()
            for (item in node) {
                copy.add(item[field])
            }
            copy
        }

        else -> node
    }
}


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
            val answer = prompt(parameterData)

            // Add answer to result and to list of variables
            answers.set<JsonNode>(field, answer)
            variables[field] = answer
        }

        return answers
    }
}

