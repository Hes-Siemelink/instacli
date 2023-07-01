package instacli.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.cli.CLI_FILE_EXTENSION
import java.util.*

abstract class CommandHandler(open val name: String) {

    open fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        when (data) {
            is ValueNode -> {
                return if (this is ValueHandler) {
                    try {
                        execute(data, context)
                    } catch (e: Exception) {
                        throw CliScriptException("An error occurred evaluating this command:", getCommand(data), e);
                    }
                } else {
                    throw CliScriptException("Command: '$name' does not handle simple text content.", getCommand(data))
                }
            }

            is ObjectNode -> {
                return if (this is ObjectHandler) {
                    try {
                        execute(data, context)
                    } catch (e: Exception) {
                        throw CliScriptException("An error occurred evaluating this command:", getCommand(data), e);
                    }
                } else {
                    throw CliScriptException("Command '$name' does not handle object content.", getCommand(data))
                }
            }

            is ArrayNode -> {
                return if (this is ArrayHandler) {
                    try {
                        execute(data, context)
                    } catch (e: Exception) {
                        throw CliScriptException("An error occurred evaluating this command:", getCommand(data), e);
                    }
                } else {
                    throw CliScriptException("Command '$name' does not handle array content.", getCommand(data))
                }
            }

            else -> throw CliScriptException(
                "Unknown content type ${data.javaClass.simpleName} for command '$name'",
                getCommand(data)
            )
        }
    }

    fun getParameter(data: JsonNode, parameter: String): JsonNode {
        return data.get(parameter) ?: throw CliScriptException(
            "Command '$name' needs '$parameter' field.",
            getCommand(data)
        )
    }

    fun getCommand(data: JsonNode): JsonNode {
        val node = ObjectNode(JsonNodeFactory.instance);
        node.set<JsonNode>(name, data)
        return node
    }
}


interface ValueHandler {
    fun execute(data: ValueNode, context: ScriptContext): JsonNode?
}

interface ObjectHandler {
    fun execute(data: ObjectNode, context: ScriptContext): JsonNode?
}

interface ArrayHandler {
    fun execute(data: ArrayNode, context: ScriptContext): JsonNode?
}

/**
 * Marker interface that indicates that variables should not be expanded.
 * The CommmandHandler will expand the variables at the time needed.
 */
interface DelayedVariableResolver

//
// Command names
//

/**
 * Creates command name from file name by stripping extension and converting dashes to spaces.
 */
fun asScriptCommand(commandName: String): String {
    var command = commandName

    // Strip extension
    if (command.endsWith(CLI_FILE_EXTENSION)) {
        command = command.substring(0, commandName.length - CLI_FILE_EXTENSION.length)
    }

    // Spaces for dashes
    command = command.replace('-', ' ')

    // Start with a capital
    command =
        command.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    return command
}

fun asCliCommand(commandName: String): String {
    var command = commandName

    // Strip extension
    if (command.endsWith(CLI_FILE_EXTENSION)) {
        command = command.substring(0, commandName.length - CLI_FILE_EXTENSION.length)
    }

    // Dashes for spaces
    command = command.replace(' ', '-')

    // All lower case
    command = command.lowercase(Locale.getDefault())

    return command
}