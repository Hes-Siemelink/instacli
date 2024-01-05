package instacli.cli

import instacli.cli.ArgType.*
import instacli.commands.InputInfo
import instacli.util.Yaml

class CliCommandLineOptions private constructor(
    val interactive: Boolean,
    val printOutput: Boolean,
    val help: Boolean,
    val debug: Boolean,
    val commands: List<String>,
    private val commandArgs: List<String>

) {
    val commandParameters by lazy { toParameterMap(commandArgs) }

    companion object {

        val definedOptions: InputInfo by lazy {
            InputInfo.from(Yaml.readResource("instacli-command-line-options.yaml"))
        }

        operator fun invoke(args: List<String> = emptyList()): CliCommandLineOptions {
            val (globalArgs, commands, commandArgs) = splitArguments(args)

            definedOptions.validateArgs(globalArgs)

            return CliCommandLineOptions(
                interactive = !globalArgs.contains("-q"),
                printOutput = globalArgs.contains("-o"),
                help = globalArgs.contains("--help"),
                debug = globalArgs.contains("-d"),
                commands,
                commandArgs
            )
        }
    }
}

private fun InputInfo.validateArgs(options: List<String>) {
    options.forEach {
        if (!this.contains(it)) {
            throw InvocationException("Invalid option: $it")
        }
    }
}


private enum class ArgType { GLOBAL, COMMAND, COMMAND_ARGS }

private fun splitArguments(args: List<String>): Triple<List<String>, List<String>, List<String>> {
    val globalArgs = mutableListOf<String>()
    val commands = mutableListOf<String>()
    val commandArgs = mutableListOf<String>()

    var state = GLOBAL

    for (argument in args) {
        if (state == GLOBAL) {
            if (isFlag(argument)) {
                globalArgs.add(argument)
            } else {
                state = COMMAND
            }
        }

        if (state == COMMAND) {
            if (isFlag(argument)) {
                state = COMMAND_ARGS
            } else {
                commands.add(argument)
            }
        }

        if (state == COMMAND_ARGS) {
            commandArgs.add(argument)
        }
    }

    return Triple(globalArgs, commands, commandArgs)
}

internal fun toParameterMap(args: List<String>): Map<String, String> {
    val parameters = mutableMapOf<String, String>()
    var currentArgument: String? = "default"
    for (argument in args) {
        if (isFlag(argument)) {
            currentArgument = noFlag(argument)
        } else {
            currentArgument ?: throw IllegalArgumentException("First element can't be a flag in $args")
            parameters[currentArgument] = argument
        }
    }
    return parameters
}

internal fun isFlag(item: String): Boolean {
    return item.startsWith('-')
}

internal fun noFlag(text: String): String {
    var start = 0
    for (character in text) {
        if (character == '-') {
            start++
        } else {
            break
        }
    }

    return text.substring(start)
}
