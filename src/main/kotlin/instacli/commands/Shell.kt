package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.lordcodes.turtle.ShellCommandNotFoundException
import com.lordcodes.turtle.ShellRunException
import com.lordcodes.turtle.shellRun
import instacli.script.*
import instacli.util.objectNode
import java.nio.file.Path

object Shell : CommandHandler("Shell"), ObjectHandler, ValueHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return execute(data.textValue(), context.workingDir)
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val commandLine = data.getTextParameter("resource")

        return execute(commandLine, context.scriptDir)
    }
}

private fun execute(commandLine: String, workingDir: Path): TextNode {
    val arguments = tokenizeCommandLine(commandLine)

    try {
        val output = shellRun(arguments[0], arguments.drop(1), workingDirectory = workingDir.toFile())

        return TextNode(output)

    } catch (e: ShellCommandNotFoundException) {
        throw InstacliCommandError("Command ${arguments[0]} not found in ${workingDir.toAbsolutePath()}", "shell")
    } catch (e: ShellRunException) {
        throw InstacliCommandError(e.message!!, "shell", objectNode("exitCode", e.exitCode.toString()))
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