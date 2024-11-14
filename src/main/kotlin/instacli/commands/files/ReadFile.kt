package instacli.commands.files

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.fasterxml.jackson.module.kotlin.contains
import instacli.language.*
import instacli.util.Json
import instacli.util.Yaml
import java.nio.file.Path
import kotlin.io.path.exists

object ReadFile : CommandHandler("Read file", "instacli/files"), ValueHandler, ObjectHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode {
        val file = data.toPath(context)

        return Yaml.readFile(file)
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode {
        val file = data.toPath(context)

        return Yaml.readFile(file)
    }
}

fun JsonNode.toPath(context: ScriptContext, directory: Path? = null): Path {
    return when (this) {
        is TextNode -> {
            val dir = directory ?: context.workingDir
            val file = dir.resolve(textValue())
            if (file.exists()) {
                file
            } else {
                throw InstacliCommandError(
                    "file not found",
                    "File not found: ${file.toRealPath()}",
                    Json.newObject("file", file.toRealPath().toString())
                )
            }
        }

        is ObjectNode -> {
            if (contains("file")) {
                this["file"].toPath(context)
            } else if (contains("resource")) {
                this["resource"].toPath(context, context.scriptDir)
            } else {
                throw CliScriptingException("Expected either 'file' or 'resource' property.")
            }
        }

        else -> throw CliScriptingException("Unsupported node type for files: ${javaClass.simpleName}")
    }
}
