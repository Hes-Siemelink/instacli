package instacli.cli

import instacli.cli.ArgType.*
import instacli.language.types.ParameterData
import instacli.util.Yaml
import instacli.util.toDomainObject

enum class OutputOption { NONE, YAML, JSON }

class CliCommandLineOptions private constructor(
    val interactive: Boolean,
    val printOutput: OutputOption,
    val help: Boolean,
    val debug: Boolean,
    val commands: List<String>,
    private val commandArgs: List<String>

) {
    val commandParameters by lazy { toParameterMap(commandArgs) }

    companion object {

        val definedOptions: CommandLineParameters by lazy {
            Yaml.readResource("cli/instacli-command-line-options.yaml").toDomainObject(CommandLineParameters::class)
        }

        operator fun invoke(args: List<String> = emptyList()): CliCommandLineOptions {

            val (globalArgs, commands, commandArgs) = splitArguments(args)
            val options = definedOptions.validateArgs(globalArgs)

            return CliCommandLineOptions(
                interactive = !options.contains("non-interactive"),
                printOutput = options.getOutputOption(),
                help = options.contains("help"),
                debug = options.contains("debug"),
                commands,
                commandArgs
            )
        }
    }
}

private fun CommandLineParameters.validateArgs(options: List<String>): CommandLineParameters {
    val parameters = mutableMapOf<String, ParameterData>()
    options.forEach {
        val (key, value) = getOption(it)
        parameters[key] = value
    }

    return CommandLineParameters(parameters)
}

private fun CommandLineParameters.getOption(option: String): Pair<String, ParameterData> {
    for ((key, value) in properties) {
        if (key == option || value.shortOption == option) {
            return Pair(key, value)
        }
    }
    throw CliInvocationException("Invalid option: $option")
}

private fun CommandLineParameters.getOutputOption(): OutputOption = when {
    contains("output") -> OutputOption.YAML
    contains("output-json") -> OutputOption.JSON
    else -> OutputOption.NONE
}

private enum class ArgType { GLOBAL_OPTIONS, FILENAME, COMMAND_ARGS }

private fun splitArguments(args: List<String>): Triple<List<String>, List<String>, List<String>> {
    val globalArgs = mutableListOf<String>()
    val filenames = mutableListOf<String>()
    val commandArgs = mutableListOf<String>()

    var state = GLOBAL_OPTIONS

    for (argument in args) {
        if (state == GLOBAL_OPTIONS) {
            if (isFlag(argument)) {
                globalArgs.add(normalize(argument))
            } else {
                state = FILENAME
            }
        }

        if (state == FILENAME) {
            if (isFlag(argument)) {
                state = COMMAND_ARGS
            } else {
                filenames.add(argument)
            }
        }

        if (state == COMMAND_ARGS) {
            commandArgs.add(argument)
        }
    }

    return Triple(globalArgs, filenames, commandArgs)
}

fun normalize(option: String): String {
    var index = 0
    while (option.length > index + 1 && option[index] == '-') {
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
            currentArgument ?: throw CliInvocationException("First element must be a flag in $args")
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
