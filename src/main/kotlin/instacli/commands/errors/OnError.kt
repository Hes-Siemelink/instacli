package instacli.commands.errors

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.*
import instacli.util.Yaml

object OnError : CommandHandler("On error", "instacli/errors"), ObjectHandler, DelayedResolver, ErrorHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        runErrorHandling(data, context)

        return null
    }

    fun runErrorHandling(errorHandlingSection: JsonNode, context: ScriptContext) {

        val error = context.error ?: return

        context.variables["error"] = Yaml.mapper.valueToTree(error.error)
        context.error = null

        errorHandlingSection.run(context)

        context.variables.remove("error")
    }
}

