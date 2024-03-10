package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.script.*

object AssertThat : CommandHandler("Assert that"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val condition = parseCondition(data)

        if (!condition.isTrue()) {
            throw AssertionError("Condition is false.\n${data}")
        }

        return null
    }
}

object AssertEquals : CommandHandler("Assert equals"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val actual = data["actual"] ?: throw ConditionException("Assert equals needs 'actual' field.")
        val expected = data["expected"] ?: throw ConditionException("Assert equals needs 'expected' field.")

        if (actual != expected) {
            throw AssertionError("Not equal:\n  Expected: $expected\n  Actual:   $actual")
        }

        return null
    }
}

object ExpectedOutput : CommandHandler("Expected output"), AnyHandler {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        val output = context.output
        if (output == null || output != data) {
            throw AssertionError("Unexpected output.\nExpected: ${data}\nOutput:   $output")
        }
        return null
    }
}

object TestCase : CommandHandler("Test case"), ValueHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return null
    }
}

object CodeExample : CommandHandler("Code example"), ValueHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return null
    }
}

/**
 * Records answers to be replayed in test cases for user input commands.
 */
object StockAnswers : CommandHandler("Stock answers"), ObjectHandler {
    val recordedAnswers = mutableMapOf<String, JsonNode>()

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        data.fields().forEach {
            recordedAnswers[it.key] = it.value
        }
        return null
    }
}
