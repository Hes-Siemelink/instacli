package instacli.commands

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.fasterxml.jackson.module.kotlin.contains
import com.github.kinquirer.core.Choice
import instacli.script.*
import instacli.util.*


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
        val inputInfo = InputInfo.from(data)
        val input: ObjectNode = context.variables.getOrPut(INPUT_VARIABLE) { objectNode() } as ObjectNode
        for ((name, info) in inputInfo.parameters) when {

            // Already exists
            input.contains(name) -> {
                continue
            }

            // Use default value
            info.default != null -> {
                input.set<JsonNode>(name, info.default as JsonNode)
            }

            // Ask user
            context.interactive -> {
                val answer = prompt(info)
                input.set<JsonNode>(name, answer)
            }

            else -> {
                throw CliScriptException("No value provided for: $name")
            }
        }

        return input
    }
}

/**
 * Asks user through simple text prompt
 */
class Prompt : CommandHandler("Prompt"), ValueHandler, ObjectHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return userPrompt.prompt(data.textValue())
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val parameter = ParameterInfo.from(data)

        return prompt(parameter)
    }
}

private fun prompt(parameter: ParameterInfo): JsonNode {
    return when (parameter.type) {
        "select one" -> promptChoice(parameter)
        "select multiple" -> promptChoice(parameter, true)
        "password" -> promptText(parameter, password = true)
        else -> promptText(parameter)
    }
}

private fun promptText(parameter: ParameterInfo, password: Boolean = false): JsonNode {
    return userPrompt.prompt(parameter.description, parameter.default?.asText() ?: "", password)
}

private fun promptChoice(parameter: ParameterInfo, multiple: Boolean = false): JsonNode {

    val choices = parameter.choices.map {
        if (parameter.display == null) {
            Choice(it.textValue(), it)
        } else {
            Choice(it[parameter.display].textValue(), it)
        }
    }

    val answer = userPrompt.select(parameter.description, choices, multiple)

    return onlyWithField(answer, parameter.value)
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
class PromptAll : CommandHandler("Prompt all"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val input = InputInfo.from(data)

        val answers = data.objectNode()
        for ((field, parameter) in input.parameters) {

            // Ask user
            val answer = userPrompt.prompt(parameter.description, parameter.default?.asText() ?: "")
            answers.set<JsonNode>(field, answer)
        }

        return answers
    }
}

/**
 * Returns the input as output.
 */
class Out : CommandHandler("Out"), AnyHandler {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode {
        return data
    }
}

/**
 * Records answers to be replayed in test cases for user input commands.
 */
class StockAnswers : CommandHandler("Stock answers"), ObjectHandler {
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
    var parameters: Map<String, ParameterInfo> = mutableMapOf()

    fun contains(option: String): Boolean {
        return parameters.contains(normalize(option))
    }

    fun getParameterInfo(option: String): ParameterInfo? {
        return parameters[normalize(option)]
    }

    private fun normalize(option: String) = option.replace("-", "")

    companion object {
        fun from(data: JsonNode): InputInfo {
            val inputInfo = Yaml.mapper.treeToValue(data, InputInfo::class.java)

            val shortOptions: Map<String, ParameterInfo> = inputInfo.parameters
                .filterValues { it.shortOption != null }
                .entries.associate { it.value.shortOption!! to it.value }
            inputInfo.parameters += shortOptions

            return inputInfo
        }
    }
}

class ParameterInfo {

    var description: String = ""
    var default: JsonNode? = null
    var type: String = ""
    var choices: List<JsonNode> = emptyList()
    var display: String? = null
    var value: String? = null

    @JsonProperty("short option")
    var shortOption: String? = null

    constructor()
    constructor(textValue: String) {
        description = textValue
    }

    companion object {
        fun from(data: JsonNode): ParameterInfo {
            return Yaml.mapper.treeToValue(data, ParameterInfo::class.java)
        }
    }
}