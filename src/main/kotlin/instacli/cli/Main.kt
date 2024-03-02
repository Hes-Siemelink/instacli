package instacli.cli

import instacli.script.InstacliLanguageException
import instacli.script.MissingParameterException
import instacli.script.ScriptContext
import instacli.script.addInputVariables
import instacli.util.toDisplayString
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

        if (options.commands.isEmpty()) {
            output.printUsage(CliCommandLineOptions.definedOptions)
            return
        }

        // First argument should be a valid file
        val file = targetFile(options.commands[0])

        // Create context based on the file and options.
        // A parent context can be passed for testing scenarios.
        val context = if (parent == null) {
            CliFileContext(file, interactive = options.interactive)
        } else {
            CliFileContext(file, parent)
        }

        // Run script directly or a command from a directory
        if (file.isDirectory()) {
            invokeDirectory(file, options.commands.drop(1), context, options)
        } else {
            invokeFile(CliFile(file), context, options)
        }
    }

    private fun targetFile(fileName: String): Path {
        val file = workingDir.resolve(fileName)
        if (file.exists()) {
            return file
        }

        val adjustedFile = workingDir.resolve(fileName + ".cli")
        if (adjustedFile.exists()) {
            return adjustedFile
        }

        throw CliInvocationException("Could not find command: ${fileName}")
    }

    private fun invokeFile(cliFile: CliFile, context: CliFileContext, options: CliCommandLineOptions) {

        if (options.help) {
            output.printScriptInfo(cliFile.script)
            return
        }

        context.addInputVariables(options.commandParameters)

        val output = cliFile.run(context)

        if (options.printOutput && output != null) {
            this.output.printOutput(output.toDisplayString())
        }
    }

    private fun invokeDirectory(
        cliDir: Path,
        args: List<String>,
        context: CliFileContext,
        options: CliCommandLineOptions
    ) {

        // No Instacli scripts in directory
        if (context.getAllCommands().isEmpty()) {
            throw CliInvocationException("No Instacli commands in ${cliDir.toAbsolutePath()}")
        }

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
                System.err.println(e.options.toDisplayString())
                return 1

            } catch (e: InstacliLanguageException) {
                reportError(e, options.debug)
                return 1
            }

            return 0
        }
    }
}

private fun reportError(e: InstacliLanguageException, printStackTrace: Boolean) {
    System.err.println("\nInstacli scripting error")

    // Exception caused by incorrect instacli script
    if (e.cause == null || e.cause is InstacliLanguageException) {
        System.err.println("\n${e.message}")
    } else {
        // Unexpected exception from command handler implementation
        if (printStackTrace) {
            System.err.print("\nCaused by: ")
            e.cause?.printStackTrace()
        } else {
            System.err.println("\nCaused by: ${e.cause}")
        }
    }

    // Print Instacli context
    e.data?.let {
        val yaml = e.data.toDisplayString().prependIndent("  ")
        val message = "In ${e.context ?: "command"}:"
        System.err.println("\n\n$message\n\n${yaml}".trimMargin())
    }
}
