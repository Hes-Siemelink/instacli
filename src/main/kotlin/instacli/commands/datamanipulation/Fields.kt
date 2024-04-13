package instacli.commands.datamanipulation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.CommandHandler
import instacli.language.ObjectHandler
import instacli.language.ScriptContext
import instacli.language.ValueHandler

object Fields : CommandHandler("Fields", "instacli/data-manipulation"), ObjectHandler, ValueHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val fields = data.arrayNode()

        for ((key, _) in data.fields()) {
            fields.add(key)
        }

        return fields
    }

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {

        val output = context.output

        return if (output is ObjectNode) {
            execute(output, context)
        } else {
            null
        }
    }
}