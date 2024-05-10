package instacli.commands.connections

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.*
import instacli.util.Json

object GetCredentials : CommandHandler("Get credentials", "instacli/connections"), ValueHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {

        val targetName = data.asText() ?: throw CommandFormatException("Specify target resource")
        val credentials = context.getCredentials()
        val target = credentials.targetResources[targetName] ?: return TextNode("")

        return when {
            target.default != null -> {
                target.default()
            }

            target.credentials.isNotEmpty() -> {
                target.credentials.first()
            }

            else -> throw InstacliCommandError(
                "no accounts",
                "No accounts defined for $targetName",
                Json.newObject("target", targetName)
            )
        }
    }
}