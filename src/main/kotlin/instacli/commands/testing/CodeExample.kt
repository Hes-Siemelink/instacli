package instacli.commands.testing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.CommandHandler
import instacli.language.ScriptContext
import instacli.language.ValueHandler

object CodeExample : CommandHandler("Code example", "instacli/testing"), ValueHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        ExpectedConsoleOutput.reset(context)

        return null
    }
}