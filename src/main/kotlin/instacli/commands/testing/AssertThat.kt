package instacli.commands.testing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.commands.isFalse
import instacli.commands.toCondition
import instacli.language.CommandHandler
import instacli.language.ObjectHandler
import instacli.language.ScriptContext

object AssertThat : CommandHandler("Assert that", "instacli/testing"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val condition = data.toCondition()

        if (condition.isFalse()) {
            throw AssertionError("Condition is false.\n${data}")
        }

        return null
    }
}