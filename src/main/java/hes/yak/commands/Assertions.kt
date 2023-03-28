package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import hes.yak.*

class AssertThat : CommandHandler, ListProcessor {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        val condition = parseCondition(data)

        if (!condition.isTrue()) {
            throw AssertionError("Condition is false.\n${data}")
        }

        return null
    }
}

class AssertEquals : CommandHandler, ListProcessor {

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

class ExpectedOutput : CommandHandler {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        val output: JsonNode? = context.variables["output"]
        if (output == null || output != data) {
            throw AssertionError("Unexpected output.\nExpected: ${data}\nOutput:   $output")
        }
        return null
    }
}

class TestCase : CommandHandler {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        println("Test case: ${data.asText()}")
        return null
    }
}

class Print : CommandHandler, ListProcessor {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        println(data)
        return null
    }
}