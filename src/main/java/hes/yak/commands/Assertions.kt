package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import hes.yak.*

class AssertThat : CommandHandler("Assert that"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val condition = parseCondition(data)

        if (!condition.isTrue()) {
            throw AssertionError("Condition is false.\n${data}")
        }

        return null
    }
}

class AssertEquals : CommandHandler("Assert equals"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val actual = data.get("actual") ?: throw ConditionException("Assert equals needs 'actual' field.")
        val expected = data.get("expected") ?: throw ConditionException("Assert equals needs 'expected' field.")

        if (actual != expected) {
            throw AssertionError("Not equal:\n  Expected: $expected\n  Actual:   $actual")
        }

        return null
    }
}

class ExpectedOutput : CommandHandler("Expected output") {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        val output: JsonNode? = context.variables["output"]
        if (output == null || output != data) {
            throw AssertionError("Unexpected output.\nExpected: ${data}\nOutput:   $output")
        }
        return null
    }
}

class TestCase : CommandHandler("Test case"), ValueHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        println("Test case: ${data.asText()}")
        return null
    }
}
