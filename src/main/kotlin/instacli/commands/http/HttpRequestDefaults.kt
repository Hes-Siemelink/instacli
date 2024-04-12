package instacli.commands.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.CommandHandler
import instacli.language.ObjectHandler
import instacli.language.ScriptContext
import instacli.language.ValueHandler
import instacli.util.Json

object HttpRequestDefaults : CommandHandler("Http request defaults", "instacli/http"), ObjectHandler, ValueHandler {

    private const val HTTP_DEFAULTS = "http.defaults"

    private fun store(context: ScriptContext, value: JsonNode) {
        context.session[HTTP_DEFAULTS] = value
    }

    fun getFrom(context: ScriptContext): JsonNode? {
        return context.session[HTTP_DEFAULTS] as JsonNode?
    }

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return getFrom(context) ?: Json.newObject()
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        store(context, data)
        return null
    }
}