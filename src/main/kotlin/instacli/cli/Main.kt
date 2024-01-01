package instacli.cli

import instacli.commands.Connections
import instacli.script.InstacliException
import instacli.util.Yaml
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.system.exitProcess

val INSTACLI_HOME: Path = Path.of(System.getProperty("user.home"), ".instacli")

class InvocationException(message: String) : Exception(message)

fun main(args: Array<String>) {

    val options = CliCommandLineOptions(args.toList())

    try {
        InstacliInvocation(options).invoke()
    } catch (e: InvocationException) {
        System.err.println(e.message)

        exitProcess(1)
    } catch (e: InstacliException) {
        reportError(e, options.debug)

        exitProcess(1)
    }
}

class InstacliInvocation(
    private val options: CliCommandLineOptions,
    private val workingDir: Path = Path.of("."),
    private val input: UserInput = ConsoleInput,
    private val output: UserOutput = ConsoleOutput
) {

    constructor(
        vararg args: String,
        workingDir: Path = Path.of("."),
        input: UserInput = ConsoleInput,
        output: UserOutput = ConsoleOutput
    ) : this(CliCommandLineOptions(args.toList()), workingDir, input, output)

    fun invoke() {

        if (options.commands.isEmpty()) {
            output.printUsage()
            return
        }

        // First argument should be a valid file
        val file = workingDir.resolve(options.commands[0])
        if (!file.exists()) {
            throw InvocationException("Could not find file: ${file.toAbsolutePath()}")
        }

        // Run script directly or a command from a directory
        val context = CliFileContext(file, interactive = options.interactive, connections = Connections.load())
        if (file.isDirectory()) {
            invokeDirectory(file, options.commands.drop(1), context, options)
        } else {
            invokeFile(CliFile(file), context, options)
        }
    }

    private fun invokeFile(cliFile: CliFile, context: CliFileContext, options: CliCommandLineOptions) {

        if (options.help) {
            output.printScriptInfo(cliFile.script)
            return
        }

        context.addVariables(options.commandParameters)

        val output = cliFile.runFile(context)

        if (options.printOutput && output != null) {
            this.output.println(Yaml.toString(output))
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
            throw InvocationException("No Instacli commands in ${cliDir.toAbsolutePath()}")
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
        throw InvocationException("Command '$rawCommand' not found in ${cliDir.name}")
    }

    private fun getCommand(args: List<String>, context: CliFileContext, interactive: Boolean): String? {

        // Return the command if specified
        if (args.isNotEmpty()) {
            return args[0]
        }

        // Print info
        output.printDirectoryInfo(context.info)

        // Select command
        val commands = context.getAllCommands()
        return when {
            interactive -> input.askForCommand(commands)
            else -> {
                output.printCommands(commands)
                null
            }
        }
    }
}

private fun reportError(e: InstacliException, printStackTrace: Boolean) {
    System.err.println("\nInstacli scripting error")

    // Exception caused by incorrect instacli script
    if (e.cause == null || e.cause is InstacliException) {
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
        val yaml = Yaml.toString(e.data).prependIndent("  ")
        val message = "In ${e.context ?: "command"}:"
        System.err.println("\n\n$message\n\n${yaml}".trimMargin())
    }
}
