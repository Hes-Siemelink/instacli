package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import hes.yak.*

class VariableAssignment : CommandHandler {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        for (variable in data.fields()) {
            context.variables[variable.key] = variable.value
        }
        return null
    }
}

class VariableCommandHandler(private val name: String) : CommandHandler {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        context.variables[name] = data
        return null
    }
}

class AssignOutput : CommandHandler, ListProcessor {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        if (data !is ValueNode) throw ScriptException("Command 'As' only takes text.\n$data")

        if (!context.variables.containsKey("output")) throw ScriptException("Can't assign output variable because it is empty.")

        context.variables[data.asText()] = context.variables["output"]!!

        return null
    }
}

class ApplyVariables : CommandHandler, ListProcessor {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode {
        return resolveVariables(data, context.variables)
    }
}
