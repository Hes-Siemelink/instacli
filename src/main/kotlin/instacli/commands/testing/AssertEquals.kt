package instacli.commands.testing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.commands.ConditionException
import instacli.language.CommandHandler
import instacli.language.ObjectHandler
import instacli.language.ScriptContext

object AssertEquals : CommandHandler("Assert equals", "instacli/testing"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val actual = data["actual"] ?: throw ConditionException("Assert equals needs 'actual' field.")
        val expected = data["expected"] ?: throw ConditionException("Assert equals needs 'expected' field.")

        if (actual != expected) {
            throw AssertionError("Not equal:\n  Expected: $expected\n  Actual:   $actual")
        }

        return null
    }
}