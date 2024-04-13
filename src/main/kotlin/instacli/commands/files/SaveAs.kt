package instacli.commands.files

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.CommandHandler
import instacli.language.ScriptContext
import instacli.language.ValueHandler
import instacli.util.toDisplayYaml
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createParentDirectories

object SaveAs : CommandHandler("Save as", "instacli/files"), ValueHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {

        val destinationFile = Path.of(data.textValue())
        destinationFile.createParentDirectories()

        Files.writeString(destinationFile, context.output.toDisplayYaml())

        return null
    }
}