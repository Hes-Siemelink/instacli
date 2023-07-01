package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.core.CommandHandler
import instacli.core.ScriptContext
import instacli.core.ValueHandler
import instacli.core.Yaml
import java.io.File

class ReadFile : CommandHandler("Read file"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return Yaml.readFile(File(context.scriptLocation.parent, data.textValue()))
    }
}