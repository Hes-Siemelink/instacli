package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import hes.yak.Command
import hes.yak.ConditionException
import hes.yak.ScriptContext
import hes.yak.parseCondition

class AssertThat : Command {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        val condition = parseCondition(data)

        if (!condition.isTrue()) {
            throw AssertionError("Condition is false.\n${data}")
        }

        return null
    }
}

class AssertEquals : Command {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        val actual = data.get("actual") ?: throw ConditionException("Assert equals needs 'actual' field.")
        val expected = data.get("expected") ?: throw ConditionException("Assert equals needs 'expected' field.")

        execute(actual, expected)

        return null
    }

    private fun execute(actual: JsonNode, expected: JsonNode) {
        if (actual != expected) {
            throw AssertionError("Not equal:\n  Expected: $expected\n  Actual:   $actual")
        }
    }
}

class ExpectedOutput : Command {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        if (context.output != data) {
            throw AssertionError("Unexpected output.\nExpected: ${data}\nOutput:   ${context.output}")
        }
        return null
    }
}