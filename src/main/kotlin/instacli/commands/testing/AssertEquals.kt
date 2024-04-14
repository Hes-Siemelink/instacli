package instacli.commands.testing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.CommandHandler
import instacli.language.ObjectHandler
import instacli.language.ScriptContext
import instacli.language.getParameter

object AssertEquals : CommandHandler("Assert equals", "instacli/testing"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val actual = data.getParameter("actual")
        val expected = data.getParameter("expected")

        if (actual != expected) {
            throw AssertionError("Not equal:\n  Expected: $expected\n  Actual:   $actual")
        }

        return null
    }
}