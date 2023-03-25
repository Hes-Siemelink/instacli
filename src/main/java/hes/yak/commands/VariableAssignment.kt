package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import hes.yak.Command
import hes.yak.ScriptContext

class VariableAssignment : Command {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        for (variable in data.fields()) {
            context.variables[variable.key] = variable.value
        }
        return null;
    }
}

class VariableCommand(val name: String) : Command {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        context.variables[name] = data
        return null;
    }
}