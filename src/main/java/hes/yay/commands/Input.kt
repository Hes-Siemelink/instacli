package hes.yay.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import hes.yay.core.CommandHandler
import hes.yay.core.ObjectHandler
import hes.yay.core.ScriptContext
import hes.yay.core.ScriptException

class Input : CommandHandler("Input"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        for (inputParameter in data.fields()) {
            if (context.variables.contains(inputParameter.key)) continue

            if (inputParameter.value.has("default")) {
                context.variables[inputParameter.key] = inputParameter.value.get("default")
            } else {
                throw ScriptException("Variable not provided: " + inputParameter.key)
            }
        }
        return null
    }
}

class Output : CommandHandler("Output") {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode {
        return data
    }
}