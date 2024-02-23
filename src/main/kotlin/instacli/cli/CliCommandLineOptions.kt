package instacli.cli

import instacli.cli.ArgType.*
import instacli.commands.InputData
import instacli.commands.ParameterData
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

        val definedOptions: InputData by lazy {
            InputData.from(Yaml.readResource("instacli-command-line-options.yaml"))
        }

        operator fun invoke(args: List<String> = emptyList()): CliCommandLineOptions {
            val (globalArgs, commands, commandArgs) = splitArguments(args)

            val options = definedOptions.validateArgs(globalArgs)

            return CliCommandLineOptions(
                interactive = !options.contains("non-interactive"),
                printOutput = options.contains("print-output"),
                help = options.contains("help"),
                debug = options.contains("debug"),
                commands,
                commandArgs
            )
        }
    }
}

private fun InputData.validateArgs(options: List<String>): InputData {
    val parameters = mutableMapOf<String, ParameterData>()
    options.forEach {
        val (key, value) = getOption(it)
        parameters[key] = value
    }

    return InputData(parameters)
}

private fun InputData.getOption(option: String): Pair<String, ParameterData> {
    for ((key, value) in parameters) {
        if (key == option || value.shortOption == option) {
            return Pair(key, value)
        }
    }
    throw InvocationException("Invalid option: $option")
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
                globalArgs.add(normalize(argument))
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

fun normalize(option: String): String {
    var index = 0
    while (option.length > index + 1 && option.get(index) == '-') {
        index++
    }
    return option.substring(index)
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
