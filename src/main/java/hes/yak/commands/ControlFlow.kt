package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import hes.yak.*
import java.io.File

class Do : CommandHandler("Do"), DelayedVariableResolver, ListProcessor {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        return YakScript(listOf(data), context).run()
    }
}

class If : CommandHandler("If"), ListProcessor, DelayedVariableResolver {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {

        val then = evaluateCondition(data, context) ?: return null

        return runCommand(Do(), then, context)
    }
}

// TODO rename to when
class IfAny : CommandHandler("If any"), DelayedVariableResolver {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        if (data is ArrayNode) {
            for (ifStatement in data) {
                val then = evaluateCondition(ifStatement, context) ?: continue
                return runCommand(Do(), then, context)
            }
            return null
        } else {
            throw ScriptException("Command 'If any' only takes arrays.", data)
        }
    }
}

private fun evaluateCondition(data: JsonNode, context: ScriptContext): JsonNode? {
    if (!data.has("then")) {
        throw ScriptException("Command 'If' needs a 'then' parameter.", data)
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
        throw ScriptException("Unsupported data type for if statement: ${data.javaClass.simpleName}.", data)
    }
}
class ForEach: CommandHandler("For each"), DelayedVariableResolver, ListProcessor {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode {
        if (data !is ObjectNode) throw ScriptException("Can not use For Each with text or list content.", data)

        val loopVar: String = getVariableName(data)
        val items = resolveVariables(data.remove(loopVar), context.variables)
        if (items !is ArrayNode) throw ScriptException("First field in For Each must be a list.", items)

        val output = ArrayNode(JsonNodeFactory.instance)
        for (loopData in items) {

            // Set variable
            // XXX The loop variable is NOT removed after use.
            // TODO Add a proper stack to hold the temporary variable.
            context.variables[loopVar] = loopData

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

    private fun getVariableName(data: ObjectNode): String {
        for (field in data.fields()) {
            return field.key
        }

        throw ScriptException("For each must contain a field.", data)
    }
}

class Repeat: CommandHandler("Repeat"), ListProcessor, DelayedVariableResolver {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        val actions = data.get("Do")!!
        val until = data.get("Until")!!

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

class ExecuteYayFile : CommandHandler("Execute yay file"), ListProcessor {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        val fileName = data.get("file") ?: throw ScriptException("Execute yay file needs 'file' field.", data)
        val scriptFile = File(context.scriptLocation?.parent, fileName.asText())

        return runFile(scriptFile, data)
    }

}

class ExecuteYayFileAsCommandHandler(private val scriptFile: File) : CommandHandler("{file}") {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        return runFile(scriptFile, data)
    }
}

private fun runFile(scriptFile: File, data: JsonNode): JsonNode? {
    val script = YakScript.load(scriptFile)
    for (variable in data.fields()) {
        script.context.variables[variable.key] = variable.value
    }

    return script.run()
}
