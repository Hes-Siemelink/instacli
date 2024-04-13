package instacli.commands.connections

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.CommandHandler
import instacli.language.ObjectHandler
import instacli.language.ScriptContext
import instacli.language.getTextParameter

object SetDefaultCredentials : CommandHandler("Set default credentials", "instacli/connections"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val targetName = data.getTextParameter("target")
        val newDefault = data.getTextParameter("name")
        val credentials = context.getCredentials()
        val target = credentials.targetResources[targetName] ?: return null

        target.default = newDefault
        credentials.save()

        return null
    }
}