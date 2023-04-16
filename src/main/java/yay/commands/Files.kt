package yay.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import yay.core.CommandHandler
import yay.core.ScriptContext
import yay.core.ValueHandler
import yay.core.Yaml

class ReadFile : CommandHandler("Read file"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return Yaml.readFile(data.textValue())
    }
}