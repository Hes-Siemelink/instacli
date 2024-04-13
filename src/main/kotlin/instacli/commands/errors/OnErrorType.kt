package instacli.commands.errors

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.commands.errors.OnError.runErrorHandling
import instacli.language.*

object OnErrorType : CommandHandler("On error type", "instacli/errors"), ObjectHandler, DelayedResolver, ErrorHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        for ((key, value) in data.fields()) {
            if (key == "any" || key == context.error?.error?.type) {
                runErrorHandling(value, context)
                break
            }
        }

        return null
    }
}