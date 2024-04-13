package instacli.commands.datamanipulation

import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.commands.datamanipulation.Add.add
import instacli.language.*
import instacli.util.asArray

object AddTo : CommandHandler("Add to", "instacli/data-manipulation"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): Nothing? {

        for ((key, value) in data.fields()) {
            val match = VARIABLE_REGEX.matchEntire(key)
                ?: throw CommandFormatException("Entries should be in \${..} variable syntax.")
            val varName = match.groupValues[1]

            var total = context.variables[varName] ?: throw CliScriptingException("Variable $varName not found.")
            for (item in value.asArray()) {
                total = add(total, item)
            }
            context.variables[varName] = total
        }

        return null
    }
}