package instacli.commands.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.TextNode
import instacli.language.AnyHandler
import instacli.language.CommandHandler
import instacli.language.ScriptContext

object ToJson : CommandHandler("Json", "instacli/util"), AnyHandler {

    val mapper: ObjectMapper = ObjectMapper()

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {

        val text = mapper.writeValueAsString(data).trim()
        return TextNode(text)
    }
}