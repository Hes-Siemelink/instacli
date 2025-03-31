package instacli.commands.shell

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.lordcodes.turtle.ShellCommandNotFoundException
import com.lordcodes.turtle.ShellRunException
import com.lordcodes.turtle.ShellScript
import instacli.language.*
import instacli.util.Json
import instacli.util.toDomainObject
import java.nio.file.Path

object Shell : CommandHandler("Shell", "instacli/shell"), ObjectHandler, ValueHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return execute(data.textValue(), context.workingDir)
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val commandData = data.toDomainObject(ShellCommand::class)

        val commandLine = commandData.command
            ?: commandData.resource
            ?: throw CommandFormatException("Specify shell command in either 'command' or 'resource' property")

        val dir = if (commandData.command != null) {
            context.workingDir
        } else {
            context.scriptDir
        }

        return execute(commandLine, dir, commandData.showCommand, commandData.showOutput, commandData.capture)
    }
}

private fun ObjectNode.getBooleanParameter(field: String, default: Boolean): Boolean {
    return if (has(field)) {
        this[field].asBoolean()
    } else {
        default
    }
}

private fun execute(
    commandLine: String,
    workingDir: Path,
    showCommand: Boolean = false,
    showOutput: Boolean = false,
    captureOutput: Boolean = true
): TextNode? {
    val arguments = tokenizeCommandLine(commandLine)

    try {

        if (showCommand) {
            ShellScript(dryRun = showCommand).command(arguments[0], arguments.drop(1))
        }

        // TODO Stream output with ShellScript.commandSequence
//        val output = ShellScript(workingDir.toFile()).command(arguments[0], arguments.drop(1))
        val output = ShellScript(workingDir.toFile()).commandSequence(arguments[0], arguments.drop(1))

        val buffer = StringBuilder()
        output.forEach { line ->
            if (captureOutput) {
                if (!buffer.isEmpty()) {
                    buffer.append("\n")
                }
                buffer.append(line)
            }
            if (showOutput) {
                println(line)
            }
        }

        return if (captureOutput) {
            TextNode(buffer.toString())
        } else {
            null
        }

    } catch (e: ShellCommandNotFoundException) {
        throw InstacliCommandError("shell", "Command ${arguments[0]} not found in ${workingDir.toAbsolutePath()}")
    } catch (e: ShellRunException) {
        throw InstacliCommandError("shell", e.message!!, Json.newObject("exitCode", e.exitCode.toString()))
    }
}

fun tokenizeCommandLine(line: String): List<String> {
    val arguments = mutableListOf<String>()
    var lastWord = ""
    var quote = false
    for (ch in line) {
        if (ch == '"') quote = !quote
        if (ch == ' ' && !quote) {
            arguments.add(lastWord)
            lastWord = ""
        } else
            lastWord += ch
    }
    arguments.add(lastWord)

    return arguments
}

data class ShellCommand(
    val command: String? = null,
    val resource: String? = null,

    @JsonProperty("show output")
    val showOutput: Boolean = false,

    @JsonProperty("show command")
    val showCommand: Boolean = false,

    @JsonProperty("capture output")
    val capture: Boolean = true,
)

