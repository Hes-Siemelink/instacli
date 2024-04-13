package instacli.commands.datamanipulation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.CommandHandler
import instacli.language.ObjectHandler
import instacli.language.ScriptContext

object Values : CommandHandler("Values", "instacli/data-manipulation"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val fields = data.arrayNode()

        for ((_, value) in data.fields()) {
            fields.add(value)
        }

        return fields
    }
}