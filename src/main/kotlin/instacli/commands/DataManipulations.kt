package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import instacli.script.*
import instacli.util.Yaml

class CreateObject : CommandHandler("Create object"), ArrayHandler {
    override fun execute(data: ArrayNode, context: ScriptContext): JsonNode {
        val output = data.objectNode()

        for (field in data) {
            output.set<JsonNode>(field["key"].textValue(), field["value"])
        }

        return output
    }
}

class Merge : CommandHandler("Merge"), ArrayHandler {

    override fun execute(data: ArrayNode, context: ScriptContext): JsonNode {

        var result: JsonNode = TextNode("dummy")
        var first = true

        for (item in data) {

            // Initialize result node based on first entry
            if (first) {
                if (item is ValueNode) {
                    result = ArrayNode(JsonNodeFactory.instance)
                    addValue(result, item)
                } else {
                    result = item
                }
                first = false
                continue
            }

            // Append to existing node
            if (result is ObjectNode) {
                if (item is ObjectNode) {
                    result.setAll<ObjectNode>(item)
                } else {
                    throw CommandFormatException("Can't merge array or text content with object:\nCurrent: $result\nAdding: $item")
                }
            } else if (result is ArrayNode) {
                if (item is ArrayNode) {
                    result.addAll(item)
                } else {
                    result.add(item)
                }
            }
        }

        return result
    }

    private fun addValue(
        array: ArrayNode,
        item: ValueNode
    ) {
        when {
            item.isTextual -> array.add(item.textValue())
            item.isInt -> array.add(item.intValue())
            item.isBoolean -> array.add(item.booleanValue())
            else -> array.add(item.textValue())
        }
    }
}

class Add : CommandHandler("Add"), ArrayHandler, ObjectHandler, ValueHandler {
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
