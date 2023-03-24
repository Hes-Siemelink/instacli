package hes.yak.commands
import com.fasterxml.jackson.databind.JsonNode
import hes.yak.Command
import hes.yak.ScriptContext
import hes.yak.ScriptException

class Input : Command {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        for (inputParameter in data.fields()) {
            if (context.variables.contains(inputParameter.key)) continue

            if (inputParameter.value.has("default")) {
                context.variables.put(inputParameter.key, inputParameter.value.get("default"))
            } else {
                throw ScriptException("Variable not provided: " + inputParameter.key)
            }
        }
        return null
    }
}

class Output : Command {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode {
        return data
    }

}