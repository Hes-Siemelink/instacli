package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.engine.CommandFormatException
import instacli.util.toDisplayString


fun interface Condition {
    fun isTrue(): Boolean
}

class ConditionException(message: String) : Exception(message)

class Equals(private val actual: Any, private val expected: Any) : Condition {
    override fun isTrue(): Boolean {
        return actual == expected
    }
}

class Contains(
    private val node: JsonNode,
    private val container: JsonNode
) : Condition {

    override fun isTrue(): Boolean {
        return when (container) {
            is ArrayNode -> {
                container.contains(node)
            }

            is ObjectNode -> {
                inObject(node, container)
            }

            else -> {
                throw ConditionException("You can't check if an object is in an string.")
            }
        }
    }

    private fun inObject(obj: JsonNode, container: ObjectNode): Boolean {
        if (obj is ObjectNode) {
            for (field in obj.fields()) {
                if (!container.has(field.key)) return false
                if (field.value != container[field.key]) return false
            }
            return true
        } else {
            return container.contains(obj)
        }
    }
}

class Empty(private val node: JsonNode) : Condition {
    override fun isTrue(): Boolean {
        when (node) {
            is NullNode -> return true
            is ArrayNode -> return node.isEmpty()
            is ObjectNode -> return node.isEmpty()
            is ValueNode -> return node.textValue() == null || node.textValue().isEmpty()
        }
        return node.isEmpty
    }
}

class All(private val conditions: List<Condition>) : Condition {
    override fun isTrue(): Boolean {
        return conditions.all { it.isTrue() }
    }
}

class AnyCondition(private val conditions: List<Condition>) : Condition {
    override fun isTrue(): Boolean {
        return conditions.any { it.isTrue() }
    }
}

class Not(private val condition: Condition) : Condition {
    override fun isTrue(): Boolean {
        return !condition.isTrue()
    }
}

fun parseCondition(node: JsonNode): Condition {
    when {
        node.has("object") -> {
            val obj = node["object"]

            if (node.has("equals")) {
                return Equals(obj, node["equals"])
            }

            if (node.has("in")) {
                return Contains(obj, node["in"])
            }

            throw CommandFormatException("Condition with 'object' should have either 'equals' or 'in'. Was:\n\n  ${node.toDisplayString()}")
        }

        node.has("all") -> {
            val conditions = node["all"]
            return All(conditions.map { parseCondition(it) })
        }

        node.has("any") -> {
            val conditions = node["any"]
            return AnyCondition(conditions.map { parseCondition(it) })
        }

        node.has("not") -> {
            val condition = node["not"]
            return Not(parseCondition(condition))
        }

        node.has("empty") -> {
            return Empty(node["empty"])
        }

        else -> {
            throw CommandFormatException("Condition needs 'object', 'all', 'any', 'not' or empty. Was:\n\n  ${node.toDisplayString()}")
        }
    }
}
