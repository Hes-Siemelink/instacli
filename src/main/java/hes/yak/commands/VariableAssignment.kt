package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import hes.yak.*

class VariableAssignment : CommandHandler("Set"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        for (variable in data.fields()) {
            context.variables[variable.key] = variable.value
        }
        return null
    }
}

class VariableCommandHandler(private val varName: String) : CommandHandler("\${}") {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        context.variables[varName] = data
        return null
    }
}

class AssignOutput : CommandHandler("As"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {

        if (!context.variables.containsKey("output")) {
            throw ScriptException("Can't assign output variable because it is empty.", data)
        }

        context.variables[data.asText()] = context.variables["output"]!!

        return null
    }
}

class ApplyVariables : CommandHandler("Apply variables") {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode {
        return resolveVariables(data, context.variables)
    }
}
