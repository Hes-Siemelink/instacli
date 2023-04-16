package hes.yay.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import hes.yay.CommandHandler
import hes.yay.ScriptContext
import hes.yay.ValueHandler
import hes.yay.Yaml

class ReadFile : CommandHandler("Read file"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return Yaml.readFile(data.textValue())
    }
}