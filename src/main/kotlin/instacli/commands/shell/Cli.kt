package instacli.commands.shell

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.cli.InstacliMain
import instacli.commands.testing.ExpectedConsoleOutput
import instacli.language.CommandHandler
import instacli.language.ObjectHandler
import instacli.language.ScriptContext
import instacli.language.ValueHandler
import instacli.util.IO
import instacli.util.toDomainObject
import java.nio.file.Path

object Cli : CommandHandler("Cli", "instacli/shell"), ValueHandler, ObjectHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {

        return runCli(context, data.asText())
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val info = data.toDomainObject(CliData::class)

        return runCli(context, info.command, info.cd?.let { Path.of(it) } ?: context.tempDir)
    }

    private fun runCli(context: ScriptContext, command: String, workingDir: Path = context.tempDir): TextNode {
        val line = command.split("\\s+".toRegex())
        val args = line.drop(1).toTypedArray()

        // Capture console output
        val (stdout, stderr) = IO.captureSystemOutAndErr {
            InstacliMain.main(args, workingDir = workingDir)
        }

        // Make sure we can check the output
        val combined = ExpectedConsoleOutput.storeOutput(context, stdout, stderr)

        return TextNode(combined)
    }
}

data class CliData(
    val command: String,
    val cd: String? = null,
)