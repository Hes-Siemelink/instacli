package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import instacli.language.CommandFormatException
import instacli.util.toDisplayYaml

fun interface Condition {
    fun isTrue(): Boolean
}

fun Condition.isFalse(): Boolean {
    return !isTrue()
}

class Equals(private val actual: Any, private val expected: Any) : Condition {

    override fun isTrue(): Boolean {
        return actual == expected
    }
}

class Contains(
    private val node: JsonNode,
    private val container: JsonNode
) : Condition {

    override fun isTrue(): Boolean =
        when {
            container is ArrayNode -> {
                container.contains(node)
            }

            container is ObjectNode -> {
                inObject(node, container)
            }

            container is TextNode && node is TextNode -> {
                container.textValue().contains(node.textValue())
            }

            else -> {
                throw CommandFormatException("You can't check if a ${node.javaClass.simpleName} is in a ${container.javaClass.simpleName}.")
            }
        }

    private fun inObject(obj: JsonNode, container: ObjectNode): Boolean {
        if (obj is ObjectNode) {
            for (field in obj.fields()) {
                if (!container.has(field.key)) {
                    return false
                }
                if (field.value != container[field.key]) {
                    return false
                }
            }
            return true
        } else {
            return container.contains(obj)
        }
    }
}


class Empty(private val node: JsonNode) : Condition {
    override fun isTrue(): Boolean =
        when (node) {
            is NullNode -> true
            is ArrayNode -> node.isEmpty
            is ObjectNode -> node.isEmpty
            is NumericNode -> node.asInt() == 0
            is ValueNode -> node.textValue() == null || node.textValue().isEmpty()
            else -> node.isEmpty
        }
}


class All(private val conditions: List<Condition>) : Condition {
    override fun isTrue() = conditions.all { it.isTrue() }
}

class AnyCondition(private val conditions: List<Condition>) : Condition {
    override fun isTrue() = conditions.any { it.isTrue() }
}

class Not(private val condition: Condition) : Condition {
    override fun isTrue() = !condition.isTrue()
}

fun JsonNode.toCondition(): Condition {

    when {

        has("item") -> {
            val obj = get("item")

            if (has("equals")) {
                return Equals(obj, get("equals"))
            }

            if (has("in")) {
                return Contains(obj, get("in"))
            }

            throw CommandFormatException("Condition with 'object' should have either 'equals' or 'in'. Was:\n\n  ${toDisplayYaml()}")
        }

        has("all") -> {
            return All(get("all").map { it.toCondition() })
        }

        has("any") -> {
            return AnyCondition(get("any").map { it.toCondition() })
        }

        has("not") -> {
            return Not(get("not").toCondition())
        }

        has("empty") -> {
            return Empty(get("empty"))
        }

        else -> {
            throw CommandFormatException("Condition needs 'item', 'all', 'any', 'not' or empty. Was:\n\n  ${toDisplayYaml()}")
        }
    }
}
