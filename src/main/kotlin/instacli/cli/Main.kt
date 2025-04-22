package instacli.cli

import com.fasterxml.jackson.databind.JsonNode
import instacli.cli.OutputOption.JSON
import instacli.cli.OutputOption.YAML
import instacli.files.CliFile
import instacli.files.CliFileContext
import instacli.language.*
import instacli.language.types.toDisplayString
import instacli.util.Json
import instacli.util.add
import instacli.util.toDisplayJson
import instacli.util.toDisplayYaml
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.system.exitProcess

object InstacliPaths {
    val INSTACLI_HOME: Path = Path.of(System.getProperty("user.home"), ".instacli")
}

class CliInvocationException(message: String) : Exception(message)

fun main(args: Array<String>) {
    val status = InstacliMain.main(args)
    exitProcess(status)
}

class InstacliMain(
    private val options: CliCommandLineOptions,
    private val workingDir: Path = Path.of("."),
    private val input: UserInput = StandardInput,
    private val output: ConsoleOutput = StandardOutput
) {

    constructor(
        vararg args: String,
        workingDir: Path = Path.of("."),
        input: UserInput = StandardInput,
        output: ConsoleOutput = StandardOutput
    ) : this(CliCommandLineOptions(args.toList()), workingDir, input, output)

    fun run(parent: ScriptContext? = null) {

        // Print usage when no commands are given
        if (options.commands.isEmpty()) {
            output.printUsage(CliCommandLineOptions.definedOptions)
            return
        }

        // First argument should be a valid file
        val file = targetFile(options.commands[0])

        // Create context based on the file and options.
        // A parent context can be passed for testing scenarios.
        val context = if (parent == null) {
            CliFileContext(file, interactive = options.interactive, workingDir = workingDir)
        } else {
            CliFileContext(file, parent)
        }

        // Run script directly or a command from a directory
        if (file.isDirectory()) {
            invokeDirectory(file, options.commands.drop(1), context, options)
        } else {
            val cliFile = CliFile(file)
            invokeFile(cliFile, context, options)
        }
    }

    private fun targetFile(filename: String): Path {
        // Command is filename
        workingDir.resolve(filename).let {
            if (it.exists()) {
                return it
            }
        }

        // Append '.cli'
        workingDir.resolve("$filename.cli").let {
            if (it.exists()) {
                return it
            }
        }

        throw CliInvocationException("Could not find command: $filename")
    }

    private fun invokeFile(cliFile: CliFile, context: CliFileContext, options: CliCommandLineOptions) {

        if (options.help) {
            output.printScriptInfo(cliFile.script)
            return
        }

        context.getInputVariables().add(options.commandParameters)

        val output = cliFile.script.run(context)

        when (options.printOutput) {
            YAML -> this.output.printOutput(output.toDisplayYaml())
            JSON -> this.output.printOutput(output.toDisplayJson())
            else -> {}
        }
    }

    private fun invokeDirectory(
        cliDir: Path,
        args: List<String>,
        context: CliFileContext,
        options: CliCommandLineOptions
    ) {

        // Parse command
        val rawCommand = getCommand(args, context, context.interactive) ?: return

        // Run script
        val script = context.getCliScriptFile(rawCommand)
        if (script != null) {
            invokeFile(script, context, options)
            return
        }

        // Run subcommand
        val subcommand = context.getSubcommand(rawCommand)
        if (subcommand != null) {
            invokeDirectory(subcommand.dir, args.drop(1), CliFileContext(subcommand.dir, context), options)
            return
        }

        // Command not found
        throw CliInvocationException("Command '$rawCommand' not found in ${cliDir.name}")
    }

    private fun getCommand(args: List<String>, context: CliFileContext, interactive: Boolean): String? {

        // Return the command if specified
        if (args.isNotEmpty()) {
            return args[0]
        }


        // Print info
        output.printDirectoryInfo(context.info)

        // Select command
        val commands = context.getAllCommands().filter { !it.hidden }
        return when {
            interactive && !options.help -> input.askForCommand(commands)
            else -> {
                output.printCommands(commands)
                null
            }
        }
    }

    companion object {
        fun main(args: Array<String>, workingDir: Path = Path.of(".")): Int {

            val options = try {
                CliCommandLineOptions(args.toList())
            } catch (e: CliInvocationException) {
                System.err.println(e.message)
                return 1
            }

            try {
                InstacliMain(options, workingDir = workingDir).run()

            } catch (e: CliInvocationException) {
                System.err.println(e.message)
                return 1

            } catch (e: MissingParameterException) {
                System.err.println("Missing parameter: --${e.name}")
                System.err.println("\nOptions:")
                System.err.println(e.parameters.toDisplayString())
                return 1

            } catch (e: InstacliLanguageException) {
                e.reportError(options.debug)
                return 1

            } catch (e: InstacliCommandError) {
                e.reportError()
                return 1
            }

            return 0
        }
    }
}

fun InstacliLanguageException.reportError(printStackTrace: Boolean) {
    System.err.println("\nInstacli scripting error")

    // Exception caused by incorrect instacli script
    if (cause == null || cause is InstacliLanguageException) {
        System.err.println("\n${message}")
    } else {
        // Unexpected exception from command handler implementation
        if (printStackTrace) {
            System.err.print("\nCaused by: ")
            cause?.printStackTrace()
        } else {
            System.err.println("\nCaused by: $cause")
        }
    }

    // Print Instacli context
    data?.let {
        val yaml = data.toDisplayYaml().prependIndent("  ")
        val message = "In ${context ?: "command"}:"
        System.err.println("\n\n$message\n\n${yaml}".trimMargin())
    }
}

fun InstacliCommandError.reportError() {
    System.err.println(message)
    if (message != error.message) {
        System.err.println(error.message)
    }

    if (error.data != null) {
        val details = Json.newObject().set<JsonNode>(error.type, error.data)
        System.err.println(details.toDisplayYaml())
    }
}