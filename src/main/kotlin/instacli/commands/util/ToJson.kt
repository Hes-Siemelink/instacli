package instacli.commands.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import instacli.language.AnyHandler
import instacli.language.CommandHandler
import instacli.language.ScriptContext
import instacli.util.toCompactJson

object ToJson : CommandHandler("Json", "instacli/util"), AnyHandler {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        return TextNode(data.toCompactJson())
    }
}