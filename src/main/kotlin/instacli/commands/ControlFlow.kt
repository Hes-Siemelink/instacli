package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.script.*

object Do : CommandHandler("Do"), ObjectHandler, DelayedResolver {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return data.runScript(context)
    }
}

object Exit : CommandHandler("Exit"), AnyHandler {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        throw Break(data)
    }
}

object If : CommandHandler("If"), ObjectHandler, DelayedResolver {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val then = evaluateCondition(data, context) ?: return null

        return then.runScript(context)
    }
}

object When : CommandHandler("When"), ArrayHandler, DelayedResolver {
    override fun execute(data: ArrayNode, context: ScriptContext): JsonNode? {
        for (ifStatement in data) {

            if (ifStatement !is ObjectNode) {
                throw CommandFormatException("Unsupported data type for if statement: ${ifStatement.javaClass.simpleName}.")
            }

            // Single 'else'
            if (ifStatement.size() == 1) {
                ifStatement.get("else")?.let {
                    return it.runScript(context)
                }
            }

            // Regular 'if'
            val then = evaluateCondition(ifStatement, context) ?: continue
            return then.runScript(context)
        }
        return null
    }
}

private fun evaluateCondition(data: ObjectNode, context: ScriptContext): JsonNode? {
    val then = data.remove("then") ?: throw CommandFormatException("Command 'If' needs a 'then' parameter.")
    val else_: JsonNode? = data.remove("else")

    val condition = parseCondition(data.resolveVariables(context.variables))
    return if (condition.isTrue()) {
        then
    } else {
        else_
    }
}

private val FOR_EACH_VARIABLE = Regex(VARIABLE_REGEX.pattern + " in")

object ForEach : CommandHandler("For each"), ObjectHandler, DelayedResolver {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode {

        val (loopVar, itemList) = removeLoopVariable(data) ?: Pair("item", context.output)
        checkNotNull(itemList) { "For each without loop variable takes items from  \${output}, but \${output} is null" }
        val itemListExpanded = itemList.resolveVariables(context.variables)
        val items = toArrayNode(itemListExpanded)

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


fun toArrayNode(node: JsonNode): ArrayNode {
    when (node) {
        is ArrayNode -> return node
        is ValueNode -> return ArrayNode(JsonNodeFactory.instance).add(node)
        is ObjectNode -> {
            val array = ArrayNode(JsonNodeFactory.instance)
            for (field in node.fields()) {
                val obj: ObjectNode = array.objectNode()
                obj.set<JsonNode>("key", array.textNode(field.key))
                obj.set<JsonNode>("value", field.value)
                array.add(obj)
            }
            return array
        }
    }
    throw AssertionError("Unsupported node type ${node.javaClass}")
}


object Repeat : CommandHandler("Repeat"), ObjectHandler, DelayedResolver {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val until = data.remove("until") ?: throw CommandFormatException("Repeat needs 'Until'")

        var finished = false
        while (!finished) {
            val result = data.deepCopy().runScript(context) ?: context.output

            if (until is ObjectNode) {
                val conditions = until.deepCopy().resolveVariables(context.variables)
                finished = parseCondition(conditions).isTrue()
            } else {
                finished = (result == until)
            }
        }

        return null
    }
}
