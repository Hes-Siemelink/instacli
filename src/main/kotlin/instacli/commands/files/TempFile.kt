package instacli.commands.files

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.*
import instacli.util.toDisplayYaml
import java.nio.file.Files
import java.nio.file.Path

object TempFile : CommandHandler("Temp file", "instacli/files"), ObjectHandler, ValueHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val filename = data.get("filename")?.asText()
        val content = data.getParameter("content")

        val destinationFile = tempFile(context.tempDir, filename)

        Files.writeString(destinationFile, content.toDisplayYaml())

        return destinationFile.toJson()
    }

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        val destinationFile = tempFile(context.tempDir)

        Files.writeString(destinationFile, data.toDisplayYaml())

        return destinationFile.toJson()
    }

    private fun Path.toJson(): TextNode {
        return TextNode(this.toAbsolutePath().toString())
    }

    fun tempFile(dir: Path, filename: String? = null): Path {
        val tempFile = if (filename == null) {
            Files.createTempFile(dir, "instacli-temp-file-", "")
        } else {
            dir.resolve(filename)
        }
        tempFile.toFile().deleteOnExit()

        return tempFile
    }
}