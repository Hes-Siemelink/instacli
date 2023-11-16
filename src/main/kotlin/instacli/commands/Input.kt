package instacli.commands

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.github.kinquirer.core.Choice
import instacli.engine.*
import instacli.util.KInquirerPrompt
import instacli.util.MOCK_ANSWERS
import instacli.util.UserPrompt
import instacli.util.Yaml


var userPrompt: UserPrompt = KInquirerPrompt()

class ScriptInfo : CommandHandler("Script info"), ObjectHandler, ValueHandler, DelayedVariableResolver {
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
                continue
            }

            // Use default value
            info.default.isNotEmpty() -> {
                context.variables[name] = TextNode(info.default)
            }

            // Ask user
            context.interactive -> {
                context.variables[name] = prompt(info)
            }

            else -> {
                throw CliScriptException("No value provided for: $name")
            }
        }

        val output = data.objectNode()
        for (name in input.parameters.keys) {
            output.set<JsonNode>(name, context.variables[name])
        }
        return output
    }

}

/**
 * Asks user through simple text prompt
 */
class AskUser : CommandHandler("Ask user"), ValueHandler, ObjectHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return userPrompt.prompt(data.textValue())
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val info = InputParameterInfo.from(data)

        return prompt(info)
    }
}

private fun prompt(input: InputParameterInfo): JsonNode {
    return when (input.type) {
        "select one" -> promptChoice(input)
        "select multiple" -> promptChoice(input, true)
        "password" -> promptText(input, password = true)
        else -> promptText(input)
    }
}

private fun promptText(input: InputParameterInfo, password: Boolean = false): JsonNode {
    return userPrompt.prompt(input.description, input.default, password)
}

private fun promptChoice(input: InputParameterInfo, multiple: Boolean = false): JsonNode {

    val choices = input.choices.map {
        if (input.display == null) {
            Choice(it.textValue(), it)
        } else {
            Choice(it[input.display].textValue(), it)
        }
    }

    val answer = userPrompt.select(input.description, choices, multiple)

    return onlyWithField(answer, input.value)
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
class AskAll : CommandHandler("Ask all"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val input = InputInfo.from(data)

        val answers = data.objectNode()
        for ((field, info) in input.parameters) {

            // Ask user
            val answer = userPrompt.prompt(info.description, info.default)
            answers.set<JsonNode>(field, answer)
        }

        return answers
    }
}

/**
 * Returns the input as output.
 */
class Output : CommandHandler("Output") {
    override fun handleCommand(data: JsonNode, context: ScriptContext): JsonNode {
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
    val default: String = "",
    val type: String = "",
    val choices: List<JsonNode> = emptyList(),
    val display: String? = null,
    val value: String? = null
) {
    constructor(textValue: String) : this(description = textValue)

    companion object {
        fun from(data: JsonNode): InputParameterInfo {
            return Yaml.mapper.treeToValue(data, InputParameterInfo::class.java)
        }
    }
}