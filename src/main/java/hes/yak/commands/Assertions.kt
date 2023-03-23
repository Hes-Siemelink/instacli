package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import hes.yak.Command
import hes.yak.ScriptException

class AssertEquals : Command {

    override fun execute(node: JsonNode) {
        val actual = node.get("actual") ?: throw ConditionException("Assert equals needs 'actual' field.")
        val expected = node.get("expected") ?: throw ConditionException("Assert equals needs 'expected' field.")

        execute(actual, expected)
    }

    private fun execute(
        actual: JsonNode,
        expected: JsonNode
    ) {
        if (actual != expected) {
            throw AssertionError("Not equal:\n  Expected: $expected\n  Actual:   $actual")
        }
    }
}

class AssertThat : Command {

    override fun execute(node: JsonNode) {
        val condition = parseCondition(node)

        if (!condition.isTrue()) {
            throw AssertionError("Condition is false ${node}")
        }
    }
}

fun parseCondition(node: JsonNode): Condition {
    if (node.has("object")) {
        val obj = node.get("object")

        if (node.has("equals")) {
            return Equals(obj, node.get("equals"))
        }

        if (node.has("in")) {
            return Contains(obj, node.get("in"))
        }

        throw ScriptException("Condition with 'object' should have either 'equals' or 'in'.")
    }
    else if (node.has("all")) {
        val conditions = node.get("all")
        return All(conditions.map { parseCondition(it) })
    }
    else if (node.has("any")) {
        val conditions = node.get("any")
        return AnyCondition(conditions.map { parseCondition(it) })
    }
    else if (node.has("not")) {
        val condition = node.get("not")
        return Not(parseCondition(condition))
    }
    else {
        throw ScriptException("Condition needs 'object', 'all', 'any' or 'not'.")
    }
}

interface Condition {
    fun isTrue(): Boolean
}

class ConditionException(message: String) : Exception(message)

class Equals(val actual: Any, val expected: Any) : Condition {
    override fun isTrue(): Boolean {
        return actual == expected
    }
}

class Contains(val obj: JsonNode, val container: JsonNode) : Condition {
    override fun isTrue(): Boolean {
        if (container is ArrayNode) {
            return container.contains(obj)
        } else {
            throw ConditionException("You can only check if object is in an array.");
        }
    }

}

class All(val conditions: List<Condition>) : Condition {
    override fun isTrue(): Boolean {
        return conditions.all { it.isTrue() }
    }
}


class AnyCondition(val conditions: List<Condition>) : Condition {
    override fun isTrue(): Boolean {
        return conditions.any { it.isTrue() }
    }
}

class Not(val condition: Condition) : Condition {
    override fun isTrue(): Boolean {
        return !condition.isTrue()
    }
}
