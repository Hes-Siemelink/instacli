package instacli.script.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.promptCheckboxObject
import com.github.kinquirer.components.promptInput
import com.github.kinquirer.core.Choice
import instacli.script.execution.*
import instacli.util.Yaml

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
                context.variables[name] = promptInput(info)
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

    private fun promptInput(info: InputParameterInfo): JsonNode {
        val answer = KInquirer.promptInput(info.description)
        return TextNode(answer)
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