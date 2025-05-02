package instacli.language

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import instacli.commands.files.TempFile
import instacli.commands.files.TempFileData
import instacli.commands.scriptinfo.ScriptInfo
import instacli.commands.scriptinfo.ScriptInfoData
import instacli.commands.shell.Cli
import instacli.commands.shell.CliData
import instacli.commands.shell.Shell
import instacli.commands.shell.ShellCommand
import instacli.commands.testing.Answers
import instacli.commands.testing.ExpectedConsoleOutput
import instacli.commands.testing.TestCase
import instacli.files.MarkdownBlock
import instacli.files.MarkdownBlock.*
import instacli.util.Yaml
import instacli.util.toDomainObject
import kotlin.io.path.name

data class Command(val name: String, val data: JsonNode)

class Break(val output: JsonNode) : Exception()

class Script(val commands: List<Command>, val title: String? = null) {

    val info: ScriptInfoData? by lazy {
        getScriptInfo()
    }

    fun run(context: ScriptContext): JsonNode? {
        return try {
            ExpectedConsoleOutput.captureSystemOutAndErr(context) {
                runCommands(context)
            }
        } catch (a: Break) {
            a.output
        } catch (e: InstacliLanguageException) {
            e.context = e.context ?: context.cliFile.name
            throw e
        }
    }

    fun runCommands(context: ScriptContext): JsonNode? {
        var output: JsonNode? = null

        for (command in commands) {
            val handler = context.getCommandHandler(command.name)

            if (context.error != null && handler !is ErrorHandler) {
                continue
            }

            try {
                output = runCommand(handler, command.data, context) ?: output
            } catch (e: InstacliCommandError) {
                context.error = e
            }
        }

        context.error?.let { throw it }

        return output
    }

    private fun getScriptInfo(): ScriptInfoData? {
        val command = commands.find { it.name == ScriptInfo.name } ?: return null

        return command.data.toDomainObject(ScriptInfoData::class)
    }

    companion object {
        fun from(script: List<JsonNode>): Script {
            return Script(toCommandList(script))
        }

        fun from(data: JsonNode): Script {
            return Script(toCommandList(data))
        }

        fun from(content: String): Script {
            return Script(toCommandList(content))
        }
    }
}

private fun toCommandList(script: List<JsonNode>): List<Command> {
    return script.map { scriptNode -> toCommandList(scriptNode) }.flatten()
}

private fun toCommandList(scriptNode: JsonNode): List<Command> {
    return scriptNode.fields().asSequence().map { Command(it.key, it.value) }.toList()
}

private fun toCommandList(script: String): List<Command> {
    return toCommandList(Yaml.parseAsFile(script))
}

fun JsonNode.run(context: ScriptContext): JsonNode? {
    return Script.from(this).runCommands(context)
}

fun List<MarkdownBlock>.toScript(): Script {

    val commands = mutableListOf<Command>()
    var title: String? = null
    for (block in this) {
        when (block.type) {
            Header -> {
                title = block.headerLine.substring(block.headerLine.indexOf(' ')).trim()
            }

            YamlInstacli, HiddenYamlInstacli -> {
                commands.addAll(toCommandList(block.getContent()))
            }

            YamlFile -> {
                val data = TempFileData(
                    filename = block.getOption("file"),
                    content = TextNode(block.getContent()),
                    resolve = block.getOption("resolve")?.toBoolean() ?: false,
                )
                commands.add(
                    Command(TempFile.name, Yaml.mapper.valueToTree(data))
                )
            }

            ShellCli -> {
                if (block.headerLine.contains("ignore")) continue

                val data = CliData(
                    command = block.getContent(),
                    cd = block.getOption("cd")
                )
                commands.add(
                    Command(Cli.name, Yaml.mapper.valueToTree(data))
                )
            }

            ShellBlock -> {
                if (block.headerLine.contains("ignore")) continue

                val data = ShellCommand(
                    command = block.getContent(),
                    showOutput = block.getOption("show_output")?.toBoolean() ?: true,
                    showCommand = block.getOption("show_command")?.toBoolean() ?: false,
                    cd = block.getOption("cd")
                )
                commands.add(
                    Command(Shell.name, Yaml.mapper.valueToTree(data))
                )
            }

            MarkdownBlock.Answers -> {
                commands.add(
                    Command(Answers.name, Yaml.parse(block.getContent()))
                )
            }

            Output -> {
                commands.add(
                    Command(ExpectedConsoleOutput.name, TextNode(block.getContent()))
                )
            }
        }
    }

    return Script(commands, title)
}

/**
 * Gets all test cases as a separate script
 */
fun Script.splitTestCases(): List<Script> {

    val allTests = mutableListOf<Script>()

    var currentCase = mutableListOf<Command>()
    var testCaseFound = false
    for (command in commands) {
        if (command.name == TestCase.name) {
            if (!testCaseFound) {
                // Ignore everything before the first 'Test case' command
                testCaseFound = true
            } else {
                // Add everything that was recorded since the 'Test case' command
                allTests.add(Script(currentCase))
            }
            currentCase = mutableListOf()
        }

        currentCase.add(command)
    }

    // Add the last test case
    if (testCaseFound) {
        allTests.add(Script(currentCase))
    }

    return allTests
}
