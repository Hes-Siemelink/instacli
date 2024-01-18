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

object ScriptInfo : CommandHandler("Script info"), ObjectHandler, ValueHandler, DelayedVariableResolver {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return null
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val scriptInfoData = ScriptInfoData.from(data)
        val input = scriptInfoData.input ?: return null

        return handleInput(context, input)
    }
}

/**
 * Checks if a variable is set.
 * If not, assigns the default value.
 * Ask for a value if there is no default value and running in interactive mode.
 * Throws exception if there is no default value.
 */
private fun handleInput(
    context: ScriptContext,
    inputData: InputData
): ObjectNode {
    val input: ObjectNode = context.variables.getOrPut(INPUT_VARIABLE) { objectNode() } as ObjectNode
    for ((name, info) in inputData.parameters) when {

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


/**
 * Asks user through simple text prompt
 */
object Prompt : CommandHandler("Prompt"), ValueHandler, ObjectHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return userPrompt.prompt(data.textValue())
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val parameter = ParameterData.from(data)

        return prompt(parameter)
    }
}

private fun prompt(parameter: ParameterData): JsonNode {
    return when (parameter.type) {
        "select one" -> promptChoice(parameter)
        "select multiple" -> promptChoice(parameter, true)
        "password" -> promptText(parameter, password = true)
        else -> promptText(parameter)
    }
}

private fun promptText(parameter: ParameterData, password: Boolean = false): JsonNode {
    return userPrompt.prompt(parameter.description, parameter.default?.asText() ?: "", password)
}

private fun promptChoice(parameter: ParameterData, multiple: Boolean = false): JsonNode {

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
object PromptAll : CommandHandler("Prompt all"), ObjectHandler, DelayedVariableResolver {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        // Temporary variables that will hold the contents of the entries so later ones can refer to previous ones
        val variables = context.variables.toMutableMap()

        val answers = data.objectNode()
        for ((field, rawParameter) in data.fields()) {

            // Resolve variables
            val parameterData = ParameterData.from(resolveVariables(rawParameter, variables))

            // Only assign if condition is true
            val condition = parameterData.parseCondition()
            if (condition != null && condition.isFalse()) {
                continue
            }

            // Ask user
            val answer = prompt(parameterData)

            // Add answer to result and to list of variables
            answers.set<JsonNode>(field, answer)
            variables[field] = answer
        }

        return answers
    }
}

/**
 * Returns the input as output.
 */
object Output : CommandHandler("Output"), AnyHandler {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode {
        return data
    }
}

/**
 * Records answers to be replayed in test cases for user input commands.
 */
object StockAnswers : CommandHandler("Stock answers"), ObjectHandler {
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

class ScriptInfoData {

    var description: String? = null
    var input: InputData? = null

    constructor()
    constructor(textValue: String) {
        description = textValue
    }

    companion object {
        fun from(data: JsonNode): ScriptInfoData {
            return Yaml.mapper.treeToValue(data, ScriptInfoData::class.java)
        }
    }
}

class InputData {

    @JsonAnySetter
    var parameters: Map<String, ParameterData> = mutableMapOf()

    fun contains(option: String): Boolean {
        return parameters.contains(normalize(option))
    }

    private fun normalize(option: String) = option.replace("-", "")

    companion object {
        fun from(data: JsonNode): InputData {
            val inputData = Yaml.mapper.treeToValue(data, InputData::class.java)

            val shortOptions: Map<String, ParameterData> = inputData.parameters
                .filterValues { it.shortOption != null }
                .entries.associate { it.value.shortOption!! to it.value }
            inputData.parameters += shortOptions

            return inputData
        }
    }
}

class ParameterData {

    var description: String = ""
    var default: JsonNode? = null
    var type: String = ""
    var choices: List<JsonNode> = emptyList()
    var display: String? = null
    var value: String? = null
    var condition: JsonNode? = null

    @JsonProperty("short option")
    var shortOption: String? = null

    constructor()
    constructor(textValue: String) {
        description = textValue
    }

    fun parseCondition(): Condition? {
        return condition?.let {
            parseCondition(it)
        }
    }

    companion object {
        fun from(data: JsonNode): ParameterData {
            return Yaml.mapper.treeToValue(data, ParameterData::class.java)
        }
    }
}