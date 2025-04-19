package instacli.commands.shell

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.cli.InstacliMain
import instacli.language.CommandHandler
import instacli.language.ScriptContext
import instacli.language.ValueHandler

object Cli : CommandHandler("Cli", "instacli/shell"), ValueHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        val command = data.asText()
        val line = command.split("\\s+".toRegex())

        InstacliMain.main(line.toTypedArray(), workingDir = context.workingDir)

        return null
    }
}