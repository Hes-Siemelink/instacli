package instacli.commands.datamanipulation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.NumericNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.*
import instacli.util.toJson

object Sort : CommandHandler("Sort", "instacli/data-manipulation"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val items = data["items"]
            ?: context.output
            ?: throw CliScriptingException("Specify 'items' or make sure \${output} is set.")
        if (items !is ArrayNode) throw CommandFormatException("items should be an array")
        val sortField = data.getTextParameter("by")

        val sorted = items.sortedWith(NodeComparator(sortField))

        return sorted.toJson()
    }
}

private class NodeComparator(val field: String) : Comparator<JsonNode> {

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
