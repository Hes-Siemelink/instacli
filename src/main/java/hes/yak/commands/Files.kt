package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import hes.yak.*

class ReadFile: CommandHandler("Read file"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
            return Yaml.readFile(data.textValue())
    }
}