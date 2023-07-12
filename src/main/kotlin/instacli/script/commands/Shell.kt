package instacli.script.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.lordcodes.turtle.shellRun
import instacli.script.execution.CommandHandler
import instacli.script.execution.ObjectHandler
import instacli.script.execution.ScriptContext
import instacli.script.execution.ValueHandler

class Shell : CommandHandler("Shell"), ObjectHandler, ValueHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {

        val (command, arguments) = parseCommand(data.textValue())

        val output = shellRun(command, arguments)

        // TODO check if we can parse this into Yaml
        return TextNode(output)
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val script = getTextParameter(data, "script")
        val output = shellRun("sh", listOf(script), workingDirectory = context.scriptLocation.parentFile)

        return TextNode(output)
    }
}

internal fun parseCommand(line: String): Pair<String, List<String>> {
    val spaceIndex = line.indexOf(' ')

    return when {
        spaceIndex < 0 -> {
            Pair(line, listOf())
        }

        else -> {
            Pair(line.substring(0, spaceIndex), listOf(line.substring(spaceIndex + 1)))
        }
    }
}