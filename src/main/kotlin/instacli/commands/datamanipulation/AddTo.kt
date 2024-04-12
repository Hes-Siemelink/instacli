package instacli.commands.datamanipulation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.*

object AddTo : CommandHandler("Add to", "instacli/data-manipulation"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        for ((key, value) in data.fields()) {
            val match = VARIABLE_REGEX.matchEntire(key)
                ?: throw CommandFormatException("Entries should be in \${..} variable syntax.")
            val varName = match.groupValues[1]

            var total = context.variables[varName] ?: throw CliScriptingException("Variable $varName not found.")
            for (item in value.itemArray()) {
                total = add(total, item)
            }
            context.variables[varName] = total
        }
        return null
    }
}