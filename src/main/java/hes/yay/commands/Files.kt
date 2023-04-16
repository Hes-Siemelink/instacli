package hes.yay.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import hes.yay.core.CommandHandler
import hes.yay.core.ScriptContext
import hes.yay.core.ValueHandler
import hes.yay.core.Yaml

class ReadFile : CommandHandler("Read file"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return Yaml.readFile(data.textValue())
    }
}