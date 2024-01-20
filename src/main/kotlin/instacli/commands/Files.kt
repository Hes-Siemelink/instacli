package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.script.*
import instacli.util.Yaml
import instacli.util.toDisplayString
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists

object ReadFile : CommandHandler("Read file"), ValueHandler, ObjectHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        val file = Path.of(data.textValue())

        return if (file.exists()) {
            Yaml.readFile(file)
        } else {
            TextNode("")
        }
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val fileName = data.getTextParameter("local")
        val file = context.scriptDir.resolve(fileName)

        return if (file.exists()) {
            Yaml.readFile(file)
        } else {
            TextNode("")
        }
    }
}

object SaveAs : CommandHandler("Save as"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        val destinationFile = Path.of(data.textValue())
        destinationFile.createParentDirectories()

        val contents = context.variables[OUTPUT_VARIABLE].toDisplayString()
        Files.writeString(destinationFile, contents)

        return null
    }
}