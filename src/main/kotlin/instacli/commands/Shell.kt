package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.lordcodes.turtle.shellRun
import instacli.engine.CommandHandler
import instacli.engine.ObjectHandler
import instacli.engine.ScriptContext
import instacli.engine.ValueHandler

class Shell : CommandHandler("Shell"), ObjectHandler, ValueHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {

        val arguments = tokenizeCommandLine(data.textValue())

        val output = shellRun(arguments[0], arguments.drop(1), workingDirectory = context.cliFile.parentFile)

        // TODO check if we can parse this into Yaml
        return TextNode(output)
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val script = getTextParameter(data, "script")
        val output = shellRun("sh", listOf(script), workingDirectory = context.cliFile.parentFile)

        return TextNode(output)
    }
}


internal fun tokenizeCommandLine(line: String): List<String> {
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