package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.script.CommandHandler
import instacli.script.ScriptContext
import instacli.script.ValueHandler
import instacli.util.Yaml
import java.io.File

class ReadFile : CommandHandler("Read file"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return Yaml.readFile(File(context.cliFile.parent, data.textValue()))
    }
}