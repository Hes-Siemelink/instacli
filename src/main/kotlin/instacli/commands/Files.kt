package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.engine.CommandHandler
import instacli.engine.ScriptContext
import instacli.engine.ValueHandler
import instacli.util.Yaml
import java.io.File

class ReadFile : CommandHandler("Read file"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return Yaml.readFile(File(context.scriptLocation.parent, data.textValue()))
    }
}