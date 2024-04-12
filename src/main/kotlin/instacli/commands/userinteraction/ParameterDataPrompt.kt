package instacli.commands.userinteraction

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.kinquirer.core.Choice
import instacli.commands.ParameterData
import instacli.util.toDisplayYaml


fun ParameterData.prompt(): JsonNode {
    return when {
        enum != null && select == "single" -> promptChoice()
        enum != null && select == "multiple" -> promptChoice(multiple = true)
        type == "password" -> promptText(password = true)
        type == "boolean" -> promptBoolean()
        else -> promptText()
    }
}

private fun ParameterData.promptText(password: Boolean = false): JsonNode {
    return UserPrompt.prompt(description, default?.asText() ?: "", password)
}

private fun ParameterData.promptBoolean(): JsonNode {
    val answer = UserPrompt.prompt(description, default?.asText() ?: "")
    return if (answer.textValue() == "true") BooleanNode.TRUE else BooleanNode.FALSE
}

private fun ParameterData.promptChoice(multiple: Boolean = false): JsonNode {

    val choices = enum?.map {
        if (displayProperty == null) {
            Choice(it.toDisplayYaml(), it)
        } else {
            Choice(it[displayProperty].textValue(), it)
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


