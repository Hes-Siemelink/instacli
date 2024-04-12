package instacli.commands.datamanipulation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import instacli.language.AnyHandler
import instacli.language.CommandHandler
import instacli.language.ScriptContext

object Append : CommandHandler("Append", "instacli/data-manipulation"), AnyHandler {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        var total = context.output ?: return data
        for (item in data.itemArray()) {
            total = add(total, item)
        }
        return total
    }
}

fun JsonNode.itemArray(): ArrayNode {
    return when (this) {
        is ArrayNode -> this
        else -> ArrayNode(JsonNodeFactory.instance).add(this)
    }
}
