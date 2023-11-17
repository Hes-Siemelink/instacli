package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.script.*

class SetVariable : CommandHandler("Set variable"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        for (variable in data.fields()) {
            context.variables[variable.key] = variable.value
        }
        return null
    }
}

class AssignVariable(private val varName: String) : CommandHandler("\${}"), AnyHandler {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        context.variables[varName] = data
        return null
    }
}

class As : CommandHandler("As"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {

        if (!context.variables.containsKey(OUTPUT_VARIABLE)) {
            throw CommandFormatException("Can't assign output variable because it is empty.")
        }

        context.variables[data.asText()] = context.variables.getValue(OUTPUT_VARIABLE)

        return null
    }
}

class ApplyVariables : CommandHandler("Apply variables"), AnyHandler {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode {
        return resolveVariables(data, context.variables)
    }
}
