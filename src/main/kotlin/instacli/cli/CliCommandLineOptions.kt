package instacli.cli

import instacli.cli.ArgType.*

class CliCommandLineOptions private constructor(
    val interactive: Boolean,
    val printOutput: Boolean,
    val help: Boolean,
    val commands: List<String>,
    private val commandArgs: List<String>

) {
    val commandParameters by lazy { toParameterMap(commandArgs) }

    companion object {
        operator fun invoke(args: Array<String> = arrayOf<String>()): CliCommandLineOptions {
            val (globalArgs, commands, commandArgs) = splitArguments(args)
            val interactive = !globalArgs.contains("-q")
            val printOutput = globalArgs.contains("-o")
            val help = globalArgs.contains("--help")

            // Warn about change in command line options
            // XXX Remove this check at some point
            if (globalArgs.contains("-i")) {
                println("⚠️   -i for interactive mode is now the default and the flag is replaced by -q for non-interactive mode. ")
            }

            return CliCommandLineOptions(
                interactive,
                printOutput,
                help,
                commands,
                commandArgs
            )
        }
    }
}

private enum class ArgType { GLOBAL, COMMAND, COMMAND_ARGS }

private fun splitArguments(args: Array<String>): Triple<List<String>, List<String>, List<String>> {
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
            break;
        }
    }

    return text.substring(start)
}
