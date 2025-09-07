package instacli.cli

import specscript.cli.*
import specscript.files.CliFileContext
import specscript.language.InstacliCommandError
import specscript.language.InstacliLanguageException
import specscript.language.MissingParameterException
import specscript.language.ScriptContext
import specscript.language.types.toDisplayString
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.name

fun main(args: Array<String>) {
    System.exit(InstacliMain.main(args))
}

/**
 * Instacli CLI with interactive capabilities.
 *
 * This CLI provides:
 * - Directory browsing and navigation
 * - Interactive command selection
 * - Rich help and usage information
 * - Enhanced user experience
 */
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

        // Resolve file using shared utility
        val filename = options.commands[0]
        val resolvedFile = try {
            CliFileUtils.resolveFile(filename, workingDir)
        } catch (e: CliInvocationException) {
            throw CliInvocationException("Could not find command: $filename")
        }

        // Create context for execution
        val context = if (parent == null) {
            CliFileContext(resolvedFile, interactive = options.interactive, workingDir = workingDir)
        } else {
            CliFileContext(resolvedFile, parent)
        }

        // Handle both files and directories
        if (resolvedFile.isDirectory()) {
            invokeDirectory(resolvedFile, options.commands.drop(1), context, options)
        } else {
            // Use shared utility for file execution
            CliFileUtils.executeFile(resolvedFile, options, context, output)
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
            CliFileUtils.executeFile(script.file, options, CliFileContext(script.file, context), output)
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

        // Select command interactively
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
                CliErrorReporter.reportInvocationError(e)
                return 1
            }

            try {
                InstacliMain(options, workingDir = workingDir).run()

            } catch (e: CliInvocationException) {
                CliErrorReporter.reportInvocationError(e)
                return 1

            } catch (e: MissingParameterException) {
                System.err.println("Missing parameter: --${e.name}")
                System.err.println("\nOptions:")
                System.err.println(e.parameters.toDisplayString())
                return 1

            } catch (e: InstacliLanguageException) {
                CliErrorReporter.reportLanguageError(e, options.debug)
                return 1

            } catch (e: InstacliCommandError) {
                CliErrorReporter.reportCommandError(e)
                return 1
            }

            return 0
        }
    }
}