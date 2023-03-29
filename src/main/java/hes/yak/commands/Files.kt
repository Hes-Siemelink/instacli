package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import hes.yak.*

class ReadFile: CommandHandler("Read file"), ListProcessor {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        if (data is TextNode) {
            return readFile(data.textValue())
        } else {
            throw ScriptException("'Read file' command supports text only.", data)
        }
    }

    private fun readFile(textValue: String): JsonNode? {
        return Yaml.readFile(textValue)
    }
}