package instacli.commands.connections

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.CommandHandler
import instacli.language.InstacliCommandError
import instacli.language.ScriptContext
import instacli.language.ValueHandler
import instacli.util.Json

object GetAllCredentials : CommandHandler("Get all credentials", "instacli/connections"), ValueHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode {

        val targetName = data.asText()
        val credentials = context.getCredentials()
        val target = credentials.targetResources[targetName] ?: throw InstacliCommandError(
            "unknown target",
            "Unknown target $targetName",
            Json.newObject("target", targetName)
        )

        return target.toArrayNode()
    }
}