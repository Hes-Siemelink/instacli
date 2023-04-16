package yay.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import yay.core.*

class SetVariable : CommandHandler("Set variable"), ObjectHandler {
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

class As : CommandHandler("As"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {

        if (!context.variables.containsKey("output")) {
            throw ScriptException("Can't assign output variable because it is empty.", data)
        }

        context.variables[data.asText()] = context.variables["output"]!!

        return null
    }
}

// TODO remove 'Set' from yay
@Deprecated("Use 'As' or 'Set variable' instead", replaceWith = ReplaceWith("As()"))
class Set : CommandHandler("Set"), ValueHandler, ObjectHandler {
    private val singleAssignment = As()
    private val multipleAssignments = SetVariable()

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return singleAssignment.execute(data, context)
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return multipleAssignments.execute(data, context)
    }
}

class ApplyVariables : CommandHandler("Apply variables") {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode {
        return resolveVariables(data, context.variables)
    }
}
