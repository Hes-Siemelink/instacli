package instacli.commands.files

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.files.CliFile
import instacli.language.*
import instacli.util.Json

object RunScript : CommandHandler("Run script", "instacli.files"), ObjectHandler, ValueHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val file = data.toPath(context)
        val input = data["input"] ?: Json.newObject()

        return handleCommand(CliFile(file), input, context)
    }

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {

        val file = data.toPath(context, context.scriptDir)

        return handleCommand(CliFile(file), Json.newObject(), context)
    }
}