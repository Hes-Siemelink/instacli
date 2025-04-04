package instacli.commands.files

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.CommandHandler
import instacli.language.ObjectHandler
import instacli.language.ScriptContext
import instacli.language.ValueHandler
import instacli.util.toDisplayYaml
import java.nio.file.Files

object TempFile : CommandHandler("Temp file", "instacli/files"), ObjectHandler, ValueHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return createTempFile(data.toDisplayYaml())
    }

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return createTempFile(data.toDisplayYaml())
    }

    fun createTempFile(data: String): JsonNode? {
        val destinationFile = Files.createTempFile("instacli-temp-file-", "")
        destinationFile.toFile().deleteOnExit()

        Files.writeString(destinationFile, data)

        return TextNode(destinationFile.toAbsolutePath().toString())
    }
}