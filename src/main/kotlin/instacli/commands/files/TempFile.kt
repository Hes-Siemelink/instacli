package instacli.commands.files

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.*
import instacli.util.toDisplayYaml
import instacli.util.toDomainObject
import java.nio.file.Files
import java.nio.file.Path

object TempFile : CommandHandler("Temp file", "instacli/files"), ObjectHandler, ValueHandler, DelayedResolver {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val options = data.toDomainObject(TempFileData::class)

        return createTempFile(context, options)
    }

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return createTempFile(context, TempFileData(content = data))
    }

    private fun createTempFile(
        context: ScriptContext,
        options: TempFileData
    ): TextNode {
        val content = if (options.content is ObjectNode) {
            options.content.deepCopy()
        } else {
            options.content
        }
        val copy = if (options.resolve) {
            content.resolve(context)
        } else {
            content
        }
        val destinationFile = tempFile(context.tempDir, options.filename)

        Files.writeString(destinationFile, copy.toDisplayYaml())

        return destinationFile.toJson()
    }

    private fun Path.toJson(): TextNode {
        return TextNode(this.toAbsolutePath().toString())
    }

    fun tempFile(dir: Path, filename: String? = null): Path {
        val tempFile = if (filename == null) {
            Files.createTempFile(dir, "instacli-temp-file-", "")
        } else {
            dir.resolve(filename).apply {
                Files.createDirectories(parent)
            }
        }
        tempFile.toFile().deleteOnExit()

        return tempFile
    }
}

data class TempFileData(
    val filename: String? = null,
    val resolve: Boolean = true,
    val content: JsonNode
)