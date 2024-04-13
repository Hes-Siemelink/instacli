package instacli.commands.controlflow

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.commands.parseCondition
import instacli.language.*

object Repeat : CommandHandler("Repeat", "instacli/control-flow"), ObjectHandler, DelayedResolver {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val until = data.remove("until") ?: throw CommandFormatException("Repeat needs 'until'")

        var finished = false
        while (!finished) {
            val result = data.deepCopy().runScript(context) ?: context.output

            if (until is ObjectNode) {
                val conditions = until.deepCopy().resolve(context)
                finished = parseCondition(conditions).isTrue()
            } else {
                finished = (result == until)
            }
        }

        return null
    }
}