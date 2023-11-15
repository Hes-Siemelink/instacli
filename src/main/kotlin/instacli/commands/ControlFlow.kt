package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.engine.*

class Do : CommandHandler("Do"), ObjectHandler, DelayedVariableResolver {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return CliScript.from(data).run(context)
    }
}

class Exit : CommandHandler("Exit"), ValueHandler, ArrayHandler, ObjectHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        throw Break(data)
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        throw Break(data)
    }

    override fun execute(data: ArrayNode, context: ScriptContext): JsonNode? {
        throw Break(data)
    }
}

class If : CommandHandler("If"), ObjectHandler, DelayedVariableResolver {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val then = evaluateCondition(data, context) ?: return null

        return runCommand(Do(), then, context)
    }
}

class When : CommandHandler("When"), ArrayHandler, DelayedVariableResolver {
    override fun execute(data: ArrayNode, context: ScriptContext): JsonNode? {
        for (ifStatement in data) {
            val then = evaluateCondition(ifStatement, context) ?: continue
            return runCommand(Do(), then, context)
        }
        return null
    }
}

private fun evaluateCondition(data: JsonNode, context: ScriptContext): JsonNode? {
    if (!data.has("then")) {
        throw CommandFormatException("Command 'If' needs a 'then' parameter.", data)
    }

    if (data is ObjectNode) {

        val then = data.remove("then")!!
        val condition = parseCondition(resolveVariables(data, context.variables))
        return if (condition.isTrue()) {
            then
        } else {
            null
        }

    } else {
        throw CommandFormatException("Unsupported data type for if statement: ${data.javaClass.simpleName}.", data)
    }
}

class ForEach : CommandHandler("For each"), ObjectHandler, DelayedVariableResolver {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode {

        val (loopVar, itemList) = removeLoopVariable(data)
        val itemListExpanded = resolveVariables(itemList, context.variables)
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
            val result = Do().execute(copy, context)

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

    private fun removeLoopVariable(data: ObjectNode): Pair<String, JsonNode> {
        for (field in data.fields()) {
            data.remove(field.key)
            val match = VARIABLE_REGEX.matchEntire(field.key)
            if (match != null) {
                return Pair(match.groupValues[1], field.value)
            }

            return Pair(field.key, field.value)
        }

        throw CommandFormatException("For each must contain a field with the loop variable.", data)
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


class Repeat : CommandHandler("Repeat"), ObjectHandler, DelayedVariableResolver {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val actions = data["Do"]!!
        val until = data["Until"]!!

        var finished = false
        while (!finished) {
            val result = runCommand(Do(), actions, context)

            if (until is ObjectNode) {
                val conditions = resolveVariables(until.deepCopy(), context.variables)
                finished = parseCondition(conditions).isTrue()
            } else {
                finished = (result == until)
            }
        }
        // TODO Collect results in list like for each
        return null
    }
}
