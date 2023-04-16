package hes.yay.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import hes.yay.*

class Join : CommandHandler("Join"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        for (variable in data.fields()) {
            join(variable.key, variable.value, context.variables)
        }

        return null
    }

    private fun join(varName: String, data: JsonNode, variables: MutableMap<String, JsonNode>) {
        val content = variables[varName]

        if (content == null) {
            variables[varName] = data
            return
        }

        if (content is ValueNode) {
            throw ScriptException("Can't update value variable $varName with 'Join' command", data)
        }

        if (content is ArrayNode) {
            if (data is ArrayNode) {
                content.addAll(data)
            } else {
                content.add(data)
            }
        }

        if (content is ObjectNode) {
            if (data is ObjectNode) {
                content.setAll<ObjectNode>(data)
            } else {
                throw ScriptException(
                    "Can't update variable $varName that has object content with text or array.",
                    data
                )
            }
        }
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
                    throw ScriptException("Can't merge array or text content with object:\nCurrent: $result\nAdding: $item")
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
        if (item.isTextual) array.add(item.textValue())
        else if (item.isInt) array.add(item.intValue())
        else if (item.isBoolean) array.add(item.booleanValue())
        else array.add(item.textValue())
    }
}

// TODO: Rename fields in 'Replace'.
// Replace:
//  in: Hello me
//  find: me
//  replace with: World

class Replace : CommandHandler("Replace"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
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

        when (source) {
            is TextNode -> {
                return replaceText(source.textValue(), part, replaceWith)
            }

            is ArrayNode -> {
                return replaceArray(source, part, replaceWith)
            }

            is ObjectNode -> {
                return replaceObject(source, part, replaceWith)
            }

            else -> return null
        }

    }

    private fun replaceText(source: String, part: JsonNode, replaceWith: JsonNode): JsonNode? {
        if (part !is TextNode) {
            throw ScriptException("Replace: not allowed to find JSON content in text $source.", part)
        }

        val replacementText = if (replaceWith.isTextual) replaceWith.textValue() else Yaml.toString(replaceWith)
        val replacement = source.replace(part.textValue(), replacementText)

        return TextNode(replacement)
    }

    private fun replaceArray(source: ArrayNode, part: JsonNode, replaceWith: JsonNode): JsonNode? {
        val replacement = ArrayNode(JsonNodeFactory.instance)
        for (node in source) {
            replacement.add(replace(node, part, replaceWith))
        }
        return replacement
    }

    private fun replaceObject(source: ObjectNode, part: JsonNode, replaceWith: JsonNode): JsonNode? {
        val replacement = ObjectNode(JsonNodeFactory.instance)
        for (field in source.fields()) {
            replacement.set<JsonNode>(field.key, replace(field.value, part, replaceWith))
        }
        return replacement
    }
}
