package instacli.commands.connections

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.CommandHandler
import instacli.language.ObjectHandler
import instacli.language.ScriptContext
import instacli.util.toDomainObject

object CreateCredentials : CommandHandler("Create credentials", "instacli/connections"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode {

        val newCredentials = data.toDomainObject(CreateCredentialsInfo::class)
        val credentials = context.getCredentials()
        val target = credentials.targetResources.getOrPut(newCredentials.target) {
            TargetResource()
        }

        target.credentials.add(newCredentials.credentials)

        credentials.save()

        return newCredentials.credentials
    }
}