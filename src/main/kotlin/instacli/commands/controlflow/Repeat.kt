package instacli.commands.controlflow

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.commands.toCondition
import instacli.language.*

object Repeat : CommandHandler("Repeat", "instacli/control-flow"), ObjectHandler, DelayedResolver {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val until = data.remove("until") ?: throw CommandFormatException("Repeat needs 'until'")

        var finished = false
        while (!finished) {
            val result = data.deepCopy().run(context) ?: context.output

            if (until is ObjectNode) {
                val conditon = until.deepCopy().resolve(context).toCondition()
                finished = conditon.isTrue()
            } else {
                finished = (result == until)
            }
        }

        return null
    }
}