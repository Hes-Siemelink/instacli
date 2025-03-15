package instacli.commands.testing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.CommandHandler
import instacli.language.ScriptContext
import instacli.language.ValueHandler
import instacli.util.IO

object TestCase : CommandHandler("Test case", "instacli/testing"), ValueHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        // Capture console output for comparison later on
        val outputStream = IO.rewireSystemOut().second
        context.session[ExpectedConsoleOutput.OUTPUT] = outputStream

        return null
    }
}