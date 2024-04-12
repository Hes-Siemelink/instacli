package instacli.commands.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.CommandHandler
import instacli.language.ScriptContext
import instacli.language.ValueHandler

object Base64Decode : CommandHandler("Base64 decode", "instacli/util"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return TextNode(String(java.util.Base64.getDecoder().decode(data.asText())))
    }
}