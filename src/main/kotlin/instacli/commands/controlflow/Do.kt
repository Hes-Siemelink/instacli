package instacli.commands.controlflow

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.*

object Do : CommandHandler("Do", "instacli/control-flow"), ObjectHandler, DelayedResolver {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return data.run(context)
    }
}