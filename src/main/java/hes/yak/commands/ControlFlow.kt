package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import hes.yak.*
import java.io.File

class Do : Command, DelayedVariableResolver, ListProcessor {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        return YakScript(listOf(data), context).run()
    }
}

class ForEach: Command, DelayedVariableResolver {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        if (data !is ObjectNode) throw ScriptException("Can not use For Each with text or list content:\n${data}")

        val loopVar: String = getVariableName(data)
        val items = resolveVariables(data.remove(loopVar), context.variables)
        if (items !is ArrayNode) throw ScriptException("First field in For Each must be a list:\n${items}")

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

        throw ScriptException("For each must contain a field.\n${data}")
    }
}

class ExecuteYayFile : Command {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        val fileName = data.get("file") ?: throw ScriptException("Execute yay file needs 'file' field.")
        val scriptFile = File(context.scriptLocation?.parent, fileName.asText())

        return runFile(scriptFile, data)
    }

}

class ExecuteYayFileAsCommand(private val scriptFile: File) : Command {

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
