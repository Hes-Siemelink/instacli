package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import instacli.script.*
import instacli.util.Yaml
import instacli.util.toArrayNode

class Add : CommandHandler("Add"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val item = getParameter(data, "item")
        val target = getParameter(data, "to")

        return add(target, item)
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

class AddToOutput : CommandHandler("Add to output"), ArrayHandler, ObjectHandler, ValueHandler {
    override fun execute(data: ArrayNode, context: ScriptContext): JsonNode? {
        val output = context.variables[OUTPUT_VARIABLE] ?: return data

        when (output) {
            is ArrayNode -> output.addAll(data)
            else ->
                throw CliScriptException("Can't add an array to output of type ${output.javaClass.simpleName}")
        }

        return output
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val output = context.variables[OUTPUT_VARIABLE] ?: return data

        when (output) {
            is ObjectNode -> output.setAll<ObjectNode>(data)
            is ArrayNode -> output.add(data)
            else ->
                throw CliScriptException("Can't add an object to output of type ${output.javaClass.simpleName}")
        }

        return output
    }

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        var output = context.variables[OUTPUT_VARIABLE] ?: return data

        when (output) {
            is TextNode -> output = TextNode(output.asText() + data.asText())
            is ArrayNode -> output.add(data)
            is IntNode -> output = IntNode(output.asInt() + data.asInt())
            else ->
                throw CliScriptException("Can't add $data to output of type ${output.javaClass.simpleName}")
        }
        return output
    }
}

// TODO Sort on scalar values
class Sort : CommandHandler("Sort"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val items = getParameter(data, "items")
        if (items !is ArrayNode) throw CommandFormatException("items should be an array")
        val sortField = getTextParameter(data, "by")

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

// TODO: Rename fields in 'Replace'.
// Replace:
//  in: Hello me
//  find: me
//  replace with: World

class Replace : CommandHandler("Replace"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode {
        val source = getParameter(data, "in")
        val part = getParameter(data, "part")
        val replaceWith = getParameter(data, "with")

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
            throw CommandFormatException("'Replace: part' may contain text only")
        }

        val replacementText = Yaml.toString(replaceWith)
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

class Size : CommandHandler("Size"), ValueHandler, ArrayHandler, ObjectHandler {
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
