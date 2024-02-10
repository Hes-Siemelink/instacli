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

private fun KInquirer.renderInput(message: String, answer: String = ""): String = buildString {
    append("? ")
    append(message)
    append(" ")

    if (answer.isNotEmpty()) {
        append(answer)
    }
}

// ? Select ingredients
//  ❯ ◉ Apple
//    ◯ Banana
//    ◯ Cake
//    ◯ Drizzle
private fun KInquirer.renderInput(message: String, choices: List<Choice<JsonNode>>, answer: String): String =
    buildString {
        append("? ")
        append(message)
        append(" \n")
        choices.forEach { choice ->
            if (choice.displayName == answer) {
                append(" ❯ ◉ ")
            } else {
                append("   ◯ ")
            }
            append(choice.displayName)
            append("\n")
        }
    }

object TestPrompt : UserPrompt {

    override fun prompt(message: String, default: String, password: Boolean): JsonNode {
        val answer: JsonNode = StockAnswers.recordedAnswers[message] ?: if (default.isNotEmpty()) {
            TextNode(default)
        } else {
            throw IllegalStateException("No prerecorded answer for '$message'")
        }

        if (password) {
            println(KInquirer.renderInput(message, "********"))
        } else {
            println(KInquirer.renderInput(message, answer.toDisplayString()))
        }

        return answer
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

            println(KInquirer.renderInput(message, selection.toString()))

            return result

        } else {
            val selection = choices.find {
                selectedAnswer.textValue() == it.displayName
            } ?: throw IllegalArgumentException("Prerecorded choice '$selectedAnswer' not found in provided list.")

            println(KInquirer.renderInput(message, choices, selection.displayName))

            return selection.data

        }
    }
}

