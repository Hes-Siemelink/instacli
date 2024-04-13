package instacli.commands.datamanipulation

import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.datamanipulation.Add.add
import instacli.language.AnyHandler
import instacli.language.CommandHandler
import instacli.language.ScriptContext
import instacli.util.asArray

object Append : CommandHandler("Append", "instacli/data-manipulation"), AnyHandler {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode {

        var total = context.output ?: return data

        for (item in data.asArray()) {
            total = add(total, item)
        }

        return total
    }
}

