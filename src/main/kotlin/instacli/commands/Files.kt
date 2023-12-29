package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.cli.CliFileContext
import instacli.script.CommandHandler
import instacli.script.ScriptContext
import instacli.script.ValueHandler
import instacli.util.Yaml
import java.io.File

class ReadFile : CommandHandler("Read file"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        val dir = if (context is CliFileContext) {
            context.scriptDir
        } else {
            context.cliFile.parentFile
        }
        return Yaml.readFile(File(dir, data.textValue()))
    }
}