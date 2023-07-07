package instacli.script.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.promptCheckboxObject
import com.github.kinquirer.components.promptInput
import com.github.kinquirer.core.Choice
import instacli.script.execution.*
import instacli.util.Yaml

class ScriptInfoHandler : CommandHandler("Script info"), ObjectHandler, DelayedVariableResolver {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
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

        for (parameter in input.parameters) when {
            parameter.key in context.variables -> {
                continue
            }

            parameter.value.default.isNotEmpty() -> {
                context.variables[parameter.key] = TextNode(parameter.value.default)
            }

            parameter.value.tag.isNotEmpty() -> {
                context.variables[parameter.key] = findByTag(parameter.value, context)
            }

            context.interactive -> {
                context.variables[parameter.key] = promptInput(parameter.value)
            }

            else -> {
                throw CliScriptException("No value provided for: " + parameter.key)
            }
        }

        return null
    }

    private fun promptInput(info: InputParameterInfo): JsonNode {
        val answer = KInquirer.promptInput(info.description)
        return TextNode(answer)
    }

    private fun findByTag(
        info: InputParameterInfo,
        context: ScriptContext
    ): JsonNode {
        // For tagged parameters, pick one from the preconfigured variables with the same type
        val matchingTypes: List<JsonNode> = findMatchingTypes(context.variables.values, info.tag)
        when (matchingTypes.size) {
            1 -> {
                return matchingTypes[0]
            }

            0 -> {
                throw CliScriptException("No variables found for ${info.tag}")
            }

            else -> {
                throw CliScriptException("Multiple variables match ${info.tag}")
            }
        }
    }

    private fun findMatchingTypes(variables: Collection<JsonNode>, tag: String): List<JsonNode> {
        return variables.filter {
            it[".tag"]?.textValue() == tag
        }
    }
}

/**
 * Asks User input through interactive prompt.
 */
class UserInput : CommandHandler("User input"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val type = getParameter(data, "type")
        val message = Yaml.toString(getParameter(data, "message"))
        val choices = getParameter(data, "choices") as ArrayNode

        when (type.textValue()) {
            "checkbox" -> {
                val choicesDisplay = choices.map {
                    Choice(it["name"].textValue(), it["value"])
                }
                val answers = KInquirer.promptCheckboxObject(message, choicesDisplay)
                return Yaml.mapper.valueToTree(answers)
            }

            else -> {
                throw CliScriptException("Unsupported type for User input", data)
            }
        }
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