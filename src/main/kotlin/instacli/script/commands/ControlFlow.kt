package instacli.script.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.script.execution.*

class Header : CommandHandler("Header"), ObjectHandler, DelayedVariableResolver {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return null
    }
}

class Do : CommandHandler("Do"), ObjectHandler, DelayedVariableResolver {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return CliScript.from(data).run(context)
    }
}

class If : CommandHandler("If"), ObjectHandler, DelayedVariableResolver {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val then = evaluateCondition(data, context) ?: return null

        return runCommand(Do(), then, context)
    }
}

// TODO rename to when
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
        throw CliScriptException("Command 'If' needs a 'then' parameter.")
    }

    if (data is ObjectNode) {

        val then = data.remove("then")!!
        val condition = parseCondition(resolveVariables(data, context.variables))
        if (condition.isTrue()) {
            return then
        } else {
            return null
        }

    } else {
        throw CliScriptException("Unsupported data type for if statement: ${data.javaClass.simpleName}.")
    }
}

class ForEach : CommandHandler("For each"), ObjectHandler, DelayedVariableResolver {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode {

        val (loopVar, itemList) = removeLoopVariable(data)
        val items = resolveVariables(itemList, context.variables)
        if (items !is ArrayNode) throw CliScriptException("First field in For Each must be a list.")

        val output = ArrayNode(JsonNodeFactory.instance)
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
                output.add(result)
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

        throw CliScriptException("For each must contain a field with the loop variable.")
    }
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
