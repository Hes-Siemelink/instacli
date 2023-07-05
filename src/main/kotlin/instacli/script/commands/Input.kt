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
        val scriptInfo = CliScriptInfo.from(data)

        for (inputParameter in scriptInfo.input) when {
            inputParameter.key in context.variables -> {
                continue
            }

            inputParameter.value.default.isNotEmpty() -> {
                context.variables[inputParameter.key] = TextNode(inputParameter.value.default)
            }

            context.interactive -> {
                context.variables[inputParameter.key] = promptInput(inputParameter.value, context)
            }

            else -> {
                throw CliScriptException("Value not provided for: " + inputParameter.key)
            }
        }

        return null
    }

    private fun promptInput(
        info: InputInfo,
        context: ScriptContext
    ): JsonNode {
        if (info.type == "string") {
            val answer = KInquirer.promptInput(info.description)
            return TextNode(answer)
        } else {
            // For non-string types, pick one from the preconfigured variables with the same type
            val matchingTypes: List<JsonNode> = findMatchingTypes(context.variables.values, info.type)
            when (matchingTypes.size) {
                1 -> {
                    return matchingTypes[0]
                }

                0 -> {
                    throw CliScriptException("No variables found for ${info.type}")
                }

                else -> {
                    throw CliScriptException("Multiple variables match ${info.type}")
                }
            }
        }
    }

    private fun findMatchingTypes(variables: Collection<JsonNode>, type: String): List<JsonNode> {
        return variables.filter {
            it[".type"]?.textValue() == type
        }
    }
}

/**
 * Checks if a variable is set.
 * If not, assigns the default value.
 * Throws exception if there is no default value.
 */
class Input : CommandHandler("Input"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        for (inputParameter in data.fields()) {
            if (inputParameter.key in context.variables) continue

            if (inputParameter.value.has("default")) {
                context.variables[inputParameter.key] = inputParameter.value["default"]
            } else {
                throw CliScriptException("Variable not provided: " + inputParameter.key)
            }
        }
        return null
    }
}

/**
 * Checks if a variable is set.
 * If not, asks the user through a prompt.
 */
class CheckInput : CommandHandler("Check input"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        for (variable in data.fields()) {
            if (variable.key in context.variables.keys) continue

            val answer = KInquirer.promptInput(Yaml.toString(data[variable.key]))
            context.variables[variable.key] = TextNode(answer)
        }
        return null
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