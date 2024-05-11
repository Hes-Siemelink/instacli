package instacli.commands.userinteraction

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.BooleanNode.FALSE
import com.fasterxml.jackson.databind.node.BooleanNode.TRUE
import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.kinquirer.core.Choice
import instacli.commands.ParameterData
import instacli.util.toDisplayYaml


fun ParameterData.prompt(): JsonNode {
    return when {
        enum != null && select == "single" -> promptChoice()
        enum != null && select == "multiple" -> promptChoice(multiple = true)
        secret -> promptText(password = true)
        type == "boolean" -> promptBoolean()
        else -> promptText()
    }
}

private fun ParameterData.promptText(password: Boolean = false): JsonNode {
    return UserPrompt.prompt(description, default?.asText() ?: "", password)
}

private fun ParameterData.promptBoolean(): JsonNode {

    val answer = UserPrompt.prompt(description, default?.asText() ?: "")

    return if (answer.textValue() == "true") TRUE
    else FALSE
}

private fun ParameterData.promptChoice(multiple: Boolean = false): JsonNode {

    val choices = enum?.map { choiceData ->
        if (displayProperty == null) {
            Choice(choiceData.toDisplayYaml(), choiceData)
        } else {
            Choice(choiceData[displayProperty].textValue(), choiceData)
        }
    } ?: emptyList()

    val answer = UserPrompt.select(description, choices, multiple)

    return answer.onlyWith(valueProperty)
}

private fun JsonNode.onlyWith(field: String?): JsonNode {

    if (field == null) {
        return this
    }

    return when (this) {

        is ObjectNode -> this[field]

        is ArrayNode -> {
            val copy = arrayNode()
            for (item in this) {
                copy.add(item[field])
            }
            copy
        }

        else -> this
    }
}


