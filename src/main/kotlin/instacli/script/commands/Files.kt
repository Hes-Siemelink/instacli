package instacli.script.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.script.execution.CommandHandler
import instacli.script.execution.ScriptContext
import instacli.script.execution.ValueHandler
import instacli.util.Yaml
import java.io.File

class ReadFile : CommandHandler("Read file"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return Yaml.readFile(File(context.scriptLocation.parent, data.textValue()))
    }
}