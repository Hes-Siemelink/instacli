package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.cli.asScriptCommand
import instacli.cli.loadCliScriptFile
import instacli.core.*
import java.io.File

class Do : CommandHandler("Do"), ObjectHandler, DelayedVariableResolver {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return CliScript(listOf(data), context).run()
    }
}

class If : CommandHandler("If"), ObjectHandler, DelayedVariableResolver {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val then = evaluateCondition(data, context) ?: return null

        return runCommand(Do(), then, context)
    }
}

// TODO rename to when
class IfAny : CommandHandler("If any"), ArrayHandler, DelayedVariableResolver {
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

class ExecuteCliScriptFile : CommandHandler("Run Instacli file"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val fileName = data.get("file") ?: throw CliScriptException("Run Instacli file needs 'file' field.")
        val scriptFile = File(context.scriptLocation.parent, fileName.asText())

        return runFile(scriptFile, data)
    }

}

class ExecuteCliFileAsCommandHandler(val scriptFile: File) : CommandHandler("{file}") {

    override val name: String
        get() = asScriptCommand(scriptFile.name)

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        return runFile(scriptFile, data)
    }
}

private fun runFile(scriptFile: File, data: JsonNode): JsonNode? {
    val script = loadCliScriptFile(scriptFile)
    for (variable in data.fields()) {
        script.context.variables[variable.key] = variable.value
    }

    return script.run()
}
