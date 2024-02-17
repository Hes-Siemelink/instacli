package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.fasterxml.jackson.module.kotlin.contains
import instacli.script.*
import instacli.util.Yaml
import instacli.util.toDisplayString
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists

object ReadFile : CommandHandler("Read file"), ValueHandler, ObjectHandler {

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
                throw CliScriptException("File not found: ${file.toRealPath()}")
            }
        }

        is ObjectNode -> {
            if (contains("file")) {
                this["file"].toPath(context)
            } else if (contains("relative")) {
                this["relative"].toPath(context, context.scriptDir)
            } else {
                throw CliScriptException("Expected either 'file' or 'relative' property.");
            }
        }

        else -> throw CliScriptException("Unsupported node type for files: ${javaClass.simpleName}")
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