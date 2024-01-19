package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import instacli.script.*
import instacli.util.toArrayNode
import instacli.util.toDisplayString

object Add : CommandHandler("Add"), ArrayHandler {

    override fun execute(data: ArrayNode, context: ScriptContext): JsonNode? {
        var total: JsonNode = data.first()
        for (item in data.drop(1)) {
            total = add(total, item)
        }
        return total
    }
}

private fun asArrayNode(node: JsonNode): ArrayNode {
    return when (node) {
        is ArrayNode -> node
        else -> ArrayNode(JsonNodeFactory.instance).add(node)
    }
}

object AddTo : CommandHandler("Add to"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        for ((key, value) in data.fields()) {
            val match = VARIABLE_REGEX.matchEntire(key)
                ?: throw CliScriptException("Entries should be in \${..} variable syntax.")
            val varName = match.groupValues[1]

            var total: JsonNode = context.variables[varName] ?: throw CliScriptException("Variable $varName not found.")
            for (item in asArrayNode(value)) {
                total = add(total, item)
            }
            context.variables[varName] = total
        }
        return null
    }
}

fun add(target: JsonNode, item: JsonNode): JsonNode {
    return when (target) {
        is ArrayNode -> addToArray(target, item)
        is ObjectNode -> addToObject(target, item)
        is TextNode -> addToText(target, item)
        is IntNode -> addToInt(target, item)
        else -> throw CliScriptException("Can't add a ${item.javaClass.simpleName} to a ${target.javaClass.simpleName}")
    }
}

fun addToArray(target: ArrayNode, item: JsonNode): ArrayNode {
    return when (item) {
        is ArrayNode -> target.addAll(item)
        else -> target.add(item)
    }
}

fun addToObject(target: ObjectNode, item: JsonNode): ObjectNode {
    return when (item) {
        is ObjectNode -> target.setAll<ObjectNode>(item)
        else -> throw CliScriptException("Can't add a ${item.javaClass.simpleName} to a ${target.javaClass.simpleName}")
    }
}

fun addToText(target: TextNode, item: JsonNode): TextNode {
    return when (item) {
        is ValueNode -> TextNode(target.asText() + item.asText())
        else -> throw CliScriptException("Can't add a ${item.javaClass.simpleName} to a ${target.javaClass.simpleName}")
    }
}

fun addToInt(target: IntNode, item: JsonNode): IntNode {
    return when (item) {
        is NumericNode -> IntNode(target.asInt() + item.asInt())
        else -> throw CliScriptException("Can't add a ${item.javaClass.simpleName} to a ${target.javaClass.simpleName}")
    }
}


// TODO Sort on scalar values
object Sort : CommandHandler("Sort"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val items = data.getParameter("items")
        if (items !is ArrayNode) throw CommandFormatException("items should be an array")
        val sortField = data.getTextParameter("by")

        val sorted = items.sortedWith(NodeComparator(sortField))

        return sorted.toArrayNode()
    }
}

class NodeComparator(val field: String) : Comparator<JsonNode> {
    override fun compare(node1: JsonNode, node2: JsonNode): Int {

        val value1 = node1[field] ?: return 0
        val value2 = node2[field] ?: return 0

        return if (value1 is NumericNode && value2 is NumericNode) {
            value1.asInt() - value2.asInt()
        } else {
            value1.asText().compareTo(value2.asText())
        }
    }
}

object Replace : CommandHandler("Replace"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode {
        val source = data.getParameter("in")
        val part = data.getParameter("find")
        val replaceWith = data.getParameter("replace with")

        val result = replace(source, part, replaceWith)

        return result ?: data
    }

    private fun replace(
        source: JsonNode,
        part: JsonNode,
        replaceWith: JsonNode
    ): JsonNode? {

        return when (source) {
            is TextNode -> {
                replaceText(source.textValue(), part, replaceWith)
            }

            is ArrayNode -> {
                replaceArray(source, part, replaceWith)
            }

            is ObjectNode -> {
                replaceObject(source, part, replaceWith)
            }

            else -> null
        }

    }

    private fun replaceText(source: String, part: JsonNode, replaceWith: JsonNode): JsonNode {
        if (part !is TextNode) {
            throw CommandFormatException("'Replace.find' may contain text only")
        }

        val replacementText = replaceWith.toDisplayString()
        val replacement = source.replace(part.textValue(), replacementText)

        return TextNode(replacement)
    }

    private fun replaceArray(source: ArrayNode, part: JsonNode, replaceWith: JsonNode): JsonNode {
        val replacement = ArrayNode(JsonNodeFactory.instance)
        for (node in source) {
            replacement.add(replace(node, part, replaceWith))
        }
        return replacement
    }

    private fun replaceObject(source: ObjectNode, part: JsonNode, replaceWith: JsonNode): JsonNode {
        val replacement = source.objectNode()
        for (field in source.fields()) {
            replacement.set<JsonNode>(field.key, replace(field.value, part, replaceWith))
        }
        return replacement
    }
}

object Size : CommandHandler("Size"), ValueHandler, ArrayHandler, ObjectHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        when (data) {
            is NumericNode -> {
                return data
            }

            is BooleanNode -> {
                return if (data.booleanValue()) IntNode(1) else IntNode(0)
            }

            is TextNode -> {
                return IntNode(data.textValue().length)
            }
        }
        throw IllegalArgumentException("Unsupported type: ${data.javaClass}")
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return IntNode(data.size())
    }

    override fun execute(data: ArrayNode, context: ScriptContext): JsonNode? {
        return IntNode(data.size())
    }
}

object Find : CommandHandler("Find"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val path = data.getTextParameter("path")
        val source = data.getParameter("in")

        return source.at(toJsonPointer(path))
    }
}

object Fields : CommandHandler("Fields"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val fields = data.arrayNode()
        for ((key, _) in data.fields()) {
            fields.add(key)
        }
        return fields
    }
}

object Values : CommandHandler("Values"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val fields = data.arrayNode()
        for ((_, value) in data.fields()) {
            fields.add(value)
        }
        return fields
    }
}
