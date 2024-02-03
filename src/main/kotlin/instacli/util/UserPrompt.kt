package instacli.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.TextNode
import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.promptCheckboxObject
import com.github.kinquirer.components.promptInput
import com.github.kinquirer.components.promptInputPassword
import com.github.kinquirer.components.promptListObject
import com.github.kinquirer.core.Choice
import instacli.commands.StockAnswers

interface UserPrompt {
    fun prompt(message: String, default: String = "", password: Boolean = false): JsonNode
    fun select(message: String, choices: List<Choice<JsonNode>>, multiple: Boolean = false): JsonNode

    companion object : UserPrompt {
        var default: UserPrompt = KInquirerPrompt

        override fun prompt(message: String, default: String, password: Boolean): JsonNode {
            return UserPrompt.default.prompt(message, default, password)
        }

        override fun select(message: String, choices: List<Choice<JsonNode>>, multiple: Boolean): JsonNode {
            return UserPrompt.default.select(message, choices, multiple)
        }
    }
}

object KInquirerPrompt : UserPrompt {
    override fun prompt(message: String, default: String, password: Boolean): JsonNode {

        val answer = if (password) {
            KInquirer.promptInputPassword(message, default)
        } else {
            KInquirer.promptInput(message, default)
        }
        return TextNode(answer)
    }

    override fun select(message: String, choices: List<Choice<JsonNode>>, multiple: Boolean): JsonNode {
        if (multiple) {
            val answers = KInquirer.promptCheckboxObject(message, choices, minNumOfSelection = 1)
            return Yaml.mapper.valueToTree(answers)
        } else {
            return KInquirer.promptListObject(message, choices)
        }
    }
}


object TestPrompt : UserPrompt {
    override fun prompt(message: String, default: String, password: Boolean): JsonNode {
        var answer = StockAnswers.recordedAnswers[message]
        if (answer == null && default.isNotEmpty()) {
            answer = TextNode(default)
        }
        return answer ?: throw IllegalStateException("No prerecorded answer for '$message'")
    }

    override fun select(message: String, choices: List<Choice<JsonNode>>, multiple: Boolean): JsonNode {
        val selectedAnswer =
            StockAnswers.recordedAnswers[message] ?: throw IllegalStateException("No prerecorded answer for '$message'")

        if (multiple) {
            val set = selectedAnswer.map { it.textValue() }
            val selection = choices.filter {
                set.contains(it.displayName)
            }
            val result = ArrayNode(JsonNodeFactory.instance)
            selection.forEach { result.add(it.data) }
            return result

        } else {
            val selection = choices.find {
                selectedAnswer.textValue() == it.displayName
            }

            return selection?.data
                ?: throw IllegalArgumentException("Prerecorded choice '$selectedAnswer' not found in provided list.")
        }
    }
}