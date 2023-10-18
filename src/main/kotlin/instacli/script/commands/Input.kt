package instacli.script.commands

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.github.kinquirer.core.Choice
import instacli.script.execution.*
import instacli.util.KInquirerPrompt
import instacli.util.MOCK_ANSWERS
import instacli.util.UserPrompt
import instacli.util.Yaml


var USER_INPUT_HANDLER: UserPrompt = KInquirerPrompt()

class ScriptInfoHandler : CommandHandler("Script info"), ObjectHandler, ValueHandler, DelayedVariableResolver {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return null
    }

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return null
    }
}

/**
 * Checks if a variable is set.
 * If not, assigns the default value.
 * Ask for a value if there is no default value and running in interactive mode.
 * Throws exception if there is no default value.
 */
class Input : CommandHandler("Input"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val input = InputInfo.from(data)

        for ((name, info) in input.parameters) when {

            // Already exists
            name in context.variables -> {

                // Tag hack: expand variable with type tag
                if (info.tag.isNotEmpty()) {
                    val value = findMatchingValueByTag(context.variables, name, info.tag)
                    if (value != null) {
                        context.variables[name] = value
                    }
                }

                // Leave existing variables as-is
                continue
            }

            // Use default value
            info.default.isNotEmpty() -> {
                context.variables[name] = TextNode(info.default)
            }

            // Find by tag
            info.tag.isNotEmpty() -> {
                context.variables[name] = findByTag(context, info.tag)
            }

            // Ask user
            context.interactive -> {
                context.variables[name] = USER_INPUT_HANDLER.prompt(info.description)
            }

            else -> {
                throw CliScriptException("No value provided for: $name")
            }
        }

        return null
    }

    private fun findMatchingValueByTag(
        variables: MutableMap<String, JsonNode>,
        variableName: String,
        tag: String
    ): JsonNode? {
        val matchingVars = filterByTag(variables, tag)
        val targetVariable = variables[variableName]?.textValue()

        return matchingVars[targetVariable]
    }

    private fun findByTag(context: ScriptContext, tag: String): JsonNode {
        // For tagged parameters, pick one from the preconfigured variables with the same type
        val matchingTypes = filterByTag(context.variables, tag)
        when (matchingTypes.size) {
            1 -> {
                return matchingTypes.values.first()
            }

            0 -> {
                throw CliScriptException("No variables found for $tag")
            }

            else -> {
                throw CliScriptException("Multiple variables match $tag")
            }
        }
    }

    private fun filterByTag(variables: Map<String, JsonNode>, tag: String): Map<String, JsonNode> {
        return variables.filter {
            it.value[".tag"]?.textValue() == tag
        }
    }
}

/**
 * Asks user through simple text prompt
 */
class AskUser : CommandHandler("Ask user"), ValueHandler, ObjectHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return USER_INPUT_HANDLER.prompt(data.textValue())
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val input = QuestionInfo.from(data)

        when (input.type) {
            "select one" -> return promptChoice(input)
            "select multiple" -> return promptChoice(input, true)
            else -> return promptText(input)
        }
    }

    private fun promptText(input: QuestionInfo): JsonNode {
        return USER_INPUT_HANDLER.prompt(input.message, input.default)
    }

    private fun promptChoice(input: QuestionInfo, multiple: Boolean = false): JsonNode {

        val choices = input.choices.map {
            if (input.display == null) {
                Choice(it.textValue(), it)
            } else {
                Choice(it[input.display].textValue(), it)
            }
        }

        return USER_INPUT_HANDLER.select(input.message, choices, multiple)
    }
}

/**
 * Asks multiple questions at once
 */
class AskAll : CommandHandler("Ask all"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val input = MultipleQuestions.from(data)

        val answers = data.objectNode()
        for ((field, info) in input.fields) {

            // Ask user
            val answer = USER_INPUT_HANDLER.prompt(info.message, info.default)
            answers.set<JsonNode>(field, answer)
        }

        return answers
    }
}

/**
 * Returns the input as output.
 */
class Output : CommandHandler("Output") {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode {
        return data
    }
}

/**
 * Records answers to be replayed in test cases for user input commands.
 */
class MockAnswers : CommandHandler("Mock answers"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        data.fields().forEach {
            MOCK_ANSWERS[it.key] = it.value
        }
        return null
    }
}


//
// Data model
//

class InputInfo {

    @JsonAnySetter
    var parameters: Map<String, InputParameterInfo> = mutableMapOf()

    companion object {
        fun from(data: JsonNode): InputInfo {
            return Yaml.mapper.treeToValue(data, InputInfo::class.java)
        }
    }
}

data class InputParameterInfo(
    var description: String = "",
    val tag: String = "",
    val default: String = ""
) {
    constructor(textValue: String) : this(description = textValue)
}


class MultipleQuestions {

    @JsonAnySetter
    var fields: Map<String, QuestionInfo> = mutableMapOf()

    companion object {
        fun from(data: JsonNode): MultipleQuestions {
            return Yaml.mapper.treeToValue(data, MultipleQuestions::class.java)
        }
    }
}

data class QuestionInfo(
    var message: String = "",
    val default: String = "",
    val type: String = "",
    val choices: List<JsonNode> = emptyList(),
    val display: String? = null
) {
    constructor(textValue: String) : this(message = textValue)

    companion object {
        fun from(data: JsonNode): QuestionInfo {
            return Yaml.mapper.treeToValue(data, QuestionInfo::class.java)
        }
    }
}