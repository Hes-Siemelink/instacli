package hes.yak.commands
import com.fasterxml.jackson.databind.JsonNode
import hes.yak.CommandHandler
import hes.yak.ScriptContext
import hes.yak.ScriptException

class Input : CommandHandler("Input") {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
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