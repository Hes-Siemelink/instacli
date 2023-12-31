package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.script.CommandHandler
import instacli.script.OUTPUT_VARIABLE
import instacli.script.ScriptContext
import instacli.script.ValueHandler
import instacli.util.Yaml
import java.nio.file.Files
import java.nio.file.Path

class ReadFile : CommandHandler("Read file"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return Yaml.readFile(Path.of(data.textValue()))
    }
}

class SaveAs : CommandHandler("Save as"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        val destinationFile = Path.of(data.textValue())
        val contents = Yaml.toString(context.variables[OUTPUT_VARIABLE])
        Files.writeString(destinationFile, contents)

        return null
    }
}