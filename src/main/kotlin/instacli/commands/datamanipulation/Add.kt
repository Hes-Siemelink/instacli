package instacli.commands.datamanipulation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import instacli.language.ArrayHandler
import instacli.language.CliScriptingException
import instacli.language.CommandHandler
import instacli.language.ScriptContext

object Add : CommandHandler("Add", "instacli/data-manipulation"), ArrayHandler {

    override fun execute(data: ArrayNode, context: ScriptContext): JsonNode {
        var total: JsonNode = data.first()
        for (item in data.drop(1)) {
            total = add(total, item)
        }
        return total
    }

    fun add(target: JsonNode, item: JsonNode): JsonNode {
        return when (target) {
            is ArrayNode -> addToArray(target, item)
            is ObjectNode -> addToObject(target, item)
            is TextNode -> addToText(target, item)
            is IntNode -> addToInt(target, item)
            else -> throw CliScriptingException("Can't add a ${item.javaClass.simpleName} to a ${target.javaClass.simpleName}")
        }
    }

    private fun addToArray(target: ArrayNode, item: JsonNode): ArrayNode {
        return when (item) {
            is ArrayNode -> target.addAll(item)
            else -> target.add(item)
        }
    }

    private fun addToObject(target: ObjectNode, item: JsonNode): ObjectNode {
        return when (item) {
            is ObjectNode -> target.setAll<ObjectNode>(item)
            else -> throw CliScriptingException("Can't add a ${item.javaClass.simpleName} to a ${target.javaClass.simpleName}")
        }
    }

    private fun addToText(target: TextNode, item: JsonNode): TextNode {
        return when (item) {
            is ValueNode -> TextNode(target.asText() + item.asText())
            else -> throw CliScriptingException("Can't add a ${item.javaClass.simpleName} to a ${target.javaClass.simpleName}")
        }
    }

    private fun addToInt(target: IntNode, item: JsonNode): IntNode {
        return when (item) {
            is NumericNode -> IntNode(target.asInt() + item.asInt())
            else -> throw CliScriptingException("Can't add a ${item.javaClass.simpleName} to a ${target.javaClass.simpleName}")
        }
    }
}
