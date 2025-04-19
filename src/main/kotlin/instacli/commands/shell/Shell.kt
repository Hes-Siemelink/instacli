package instacli.commands.shell

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.*
import instacli.util.Json
import instacli.util.toDomainObject
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Path

object Shell : CommandHandler("Shell", "instacli/shell"), ObjectHandler, ValueHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        val info = ShellCommand()
        info.env["SCRIPT_DIR"] = context.scriptDir.toAbsolutePath().toString()

        return execute(data.textValue(), context.workingDir, info)
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val info = data.toDomainObject(ShellCommand::class)
        info.env["SCRIPT_DIR"] = context.scriptDir.toAbsolutePath().toString()

        val commandLine = info.command
            ?: info.resource
            ?: throw CommandFormatException("Specify shell command in either 'command' or 'resource' property")

        val dir = if (info.cd != null) {
            Path.of(info.cd)
        } else if (info.command != null) {
            context.workingDir
        } else {
            context.scriptDir
        }

        return execute(commandLine, dir, info)
    }
}

private fun ObjectNode.getBooleanParameter(field: String, default: Boolean): Boolean {
    return if (has(field)) {
        this[field].asBoolean()
    } else {
        default
    }
}

private fun execute(
    commandLine: String,
    workingDir: Path,
    info: ShellCommand = ShellCommand()
): TextNode? {

    val buffer = StringBuilder()
    try {

        if (info.showCommand) {
            println(commandLine)
        }

        val output = streamCommand(
            commandLine, workingDir, info.env
        )

        output.forEach { line ->
            if (info.captureOutput) {
                if (!buffer.isEmpty()) {
                    buffer.append("\n")
                }
                buffer.append(line)
            }
            if (info.showOutput) {
                println(line)
            }
        }

        return if (info.captureOutput) {
            TextNode(buffer.trim().toString())
        } else {
            null
        }

    } catch (e: InstacliCommandError) {
        print(buffer.toString())
        throw e
    } catch (e: IOException) {
        print(buffer.toString())
        throw InstacliCommandError("shell", e.toString())
    }
}

fun streamCommand(
    command: String,
    workingDirectory: Path? = null,
    env: Map<String, String> = emptyMap()
): Sequence<String> {

    return try {
        val processBuilder = ProcessBuilder(listOf())
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .redirectErrorStream(true)
            .command(listOf("/bin/bash", "-c", command))

        // Set working dir
        workingDirectory?.let {
            processBuilder.directory(it.toFile())
        }

        // Set environment variables
        val processEnv = processBuilder.environment()
        env.forEach { (key, value) ->
            processEnv[key] = value
        }

        // Start process
        val process = processBuilder.start()

        sequence {
            yieldAll(process.inputStream.bufferedReader().lineSequence())

            val exitCode = process.waitFor()
            if (exitCode != 0) {
                throw InstacliCommandError(
                    "shell",
                    "Shell command failed",
                    Json.newObject("exitCode", exitCode.toString())
                )

            }
        }
    } catch (exception: IOException) {
        if (exception.message?.contains("Cannot run program") == true) {
            throw FileNotFoundException()
        }
        throw exception
    }
}

data class ShellCommand(
    val command: String? = null,
    val resource: String? = null,
    val cd: String? = null,

    @JsonProperty("show output")
    val showOutput: Boolean = false,

    @JsonProperty("show command")
    val showCommand: Boolean = false,

    @JsonProperty("capture output")
    val captureOutput: Boolean = true,

    val env: MutableMap<String, String> = mutableMapOf()
) {


    companion object {

        // Regex that captures "cd:VALUE" where value is non-space characters
        val cdRegex = Regex("cd:(\\S+)")

        fun fromBlock(content: String, options: String): ShellCommand {
            val showOutput = options.contains("show_output:false")
            val showCommand = options.contains("show_command:true")
            val cd = cdRegex.find(options)?.groupValues?.get(1)
            return ShellCommand(
                command = content,
                showOutput = showOutput,
                showCommand = showCommand,
                cd = cd
            )
        }
    }
}
