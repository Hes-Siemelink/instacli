package instacli.commands.controlflow

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.*

object ForEach : CommandHandler("For each", "instacli/control-flow"), ObjectHandler, DelayedResolver {

    private val FOR_EACH_VARIABLE = Regex(VARIABLE_REGEX.pattern + " in")

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode {

        val (loopVar, itemList) = removeLoopVariable(data) ?: Pair("item", context.output)
        checkNotNull(itemList) { "For each without loop variable takes items from  \${output}, but \${output} is null" }

        val itemListExpanded = itemList.resolve(context)
        val items = itemList.resolve(context).asArrayNode()

        val output: JsonNode = if (itemListExpanded is ArrayNode) data.arrayNode() else data.objectNode()
        for (item in items) {

            // Set variable
            // XXX The loop variable is NOT removed after use.
            // TODO Add a proper stack to hold the temporary variable.
            context.variables[loopVar] = item

            // Copy the body statement because variable resolution is in-place and modifies the data
            val copy = data.deepCopy()

            // Execute
            val result = copy.runScript(context)

            if (result != null) {
                if (output is ArrayNode) {
                    output.add(result)
                } else if (output is ObjectNode) {
                    output.set<JsonNode>(item["key"].textValue(), result)
                }
            }
        }

        return output
    }

    private fun removeLoopVariable(data: ObjectNode): Pair<String, JsonNode>? {
        val first = data.fieldNames().next()
        val match = FOR_EACH_VARIABLE.matchEntire(first) ?: return null
        val items = data.remove(first)

        return Pair(match.groupValues[1], items)
    }
}

private fun JsonNode.asArrayNode(): ArrayNode {
    when (this) {
        is ArrayNode -> return this
        is ValueNode -> return ArrayNode(JsonNodeFactory.instance).add(this)
        is ObjectNode -> {
            val array = ArrayNode(JsonNodeFactory.instance)
            for (field in fields()) {
                val obj: ObjectNode = array.objectNode()
                obj.set<JsonNode>("key", array.textNode(field.key))
                obj.set<JsonNode>("value", field.value)
                array.add(obj)
            }
            return array
        }
    }
    throw AssertionError("Unsupported node type ${this.javaClass}")
}