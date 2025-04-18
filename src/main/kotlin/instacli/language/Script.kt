package instacli.language

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import instacli.commands.scriptinfo.ScriptInfo
import instacli.commands.scriptinfo.ScriptInfoData
import instacli.commands.shell.Shell
import instacli.commands.shell.ShellCommand
import instacli.commands.testing.ExpectedOutput
import instacli.commands.testing.StockAnswers
import instacli.commands.testing.TestCase
import instacli.files.InstacliMarkdown
import instacli.files.MarkdownBlock
import instacli.files.MarkdownBlock.*
import instacli.util.Yaml
import instacli.util.toDomainObject
import kotlin.io.path.name

data class Command(val name: String, val data: JsonNode)

class Break(val output: JsonNode) : Exception()

class Script(val commands: List<Command>) {

    val info: ScriptInfoData? by lazy {
        getScriptInfo()
    }

    fun run(context: ScriptContext): JsonNode? {
        return try {
            runCommands(context)
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

fun InstacliMarkdown.toScript(): Script {

    val commands = mutableListOf<Command>()
    for (block in blocks) {
        when (block.type) {
            MainText -> {}
            YamlInstacliBefore, YamlInstacliAfter, YamlInstacli -> {
                commands.addAll(toCommandList(block.getContent()))
            }

            YamlFile -> {}
            ShellCli -> {}
            MarkdownBlock.Shell -> {
                // XXX make nice and document
                val showOutput = !block.headerLine.contains("show_output=false")
                val showCommand = block.headerLine.contains("show_command=true")
                val command = ShellCommand(
                    command = block.getContent(),
                    showOutput = showOutput,
                    showCommand = showCommand
                )
                commands.add(
                    Command(Shell.name, Yaml.mapper.valueToTree(command))
                )
            }

            Input -> {
                commands.add(
                    Command(StockAnswers.name, Yaml.parse(block.getContent()))
                )
            }

            Output -> {
                commands.add(
                    Command(ExpectedOutput.name, TextNode(block.getContent()))
                )
            }
        }
    }

    return Script(commands)
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
