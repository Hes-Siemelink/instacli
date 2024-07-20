package instacli.commands.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.CommandHandler
import instacli.language.ObjectHandler
import instacli.language.ScriptContext
import instacli.language.ValueHandler
import io.ktor.http.*

object Post : CommandHandler("POST", "instacli/http"), ObjectHandler, ValueHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return HttpClient.processRequest(data, context, HttpMethod.Post)
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return HttpClient.processRequest(data, context, HttpMethod.Post)
    }
}