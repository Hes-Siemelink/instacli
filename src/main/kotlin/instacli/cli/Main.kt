package instacli.cli

import instacli.commands.Connections
import instacli.engine.InstacliException
import instacli.util.Yaml
import java.io.File
import kotlin.system.exitProcess

val INSTACLI_HOME = File(File(System.getProperty("user.home")), ".instacli")

class CliException(message: String) : Exception(message)

fun main(args: Array<String>) {

    try {
        InstacliInvocation(args).run()
    } catch (e: CliException) {
        System.err.println(e.message)

        exitProcess(1)
    } catch (e: InstacliException) {
        val cause = e.cause?.message ?: ""
        System.err.println("\nInstacli scripting error\n  $cause")
        System.err.println("  ${e.message}")

        exitProcess(1)
    }
}


class InstacliInvocation(
    private val args: Array<String>,
    private val workingDir: File = File("."),
    private val user: UserInput = ConsoleInput,
    private val out: UserOutput = ConsoleOutput
) {

    fun run() {

        val options = CliCommandLineOptions(args)

        if (options.commands.isEmpty()) {
            out.printUsage()
            return
        }

        // First argument should be a valid file
        val file = File(workingDir, options.commands[0])
        if (!file.exists()) {
            throw CliException("Could not find file: ${file.absolutePath}")
        }

        // Run script directly or a command from a directory
        val context = CliFileContext(file, interactive = options.interactive, connections = Connections.load())
        if (file.isDirectory) {
            runDirectory(file, options.commands.drop(1), context, options)
        } else {
            runFile(CliFile(file), context, options)
        }
    }

    private fun runFile(script: CliFile, context: CliFileContext, options: CliCommandLineOptions) {

        if (options.help) {
            out.printScriptInfo(script.script)
            return
        }

        context.addVariables(options.commandParameters)

        val output = script.run(context)

        if (options.printOutput && output != null) {
            out.println(Yaml.toString(output))
        }
    }

    private fun runDirectory(cliDir: File, args: List<String>, context: CliFileContext, options: CliCommandLineOptions) {

        // No Instacli scripts in directory
        if (context.getAllCommands().isEmpty()) {
            println("No Instacli commands in ${cliDir.absolutePath}")
            return
        }

        // Parse command
        val rawCommand = getCommand(args, context, context.interactive) ?: return

        // Run script
        val script = context.getCliScriptFile(rawCommand)
        if (script != null) {
            runFile(script, context, options)
            return
        }

        // Run subcommand
        val subcommand = context.getSubcommand(rawCommand)
        if (subcommand != null) {
            runDirectory(subcommand.dir, args.drop(1), CliFileContext(subcommand.dir, context), options)
            return
        }

        // Command not found
        throw CliException("Command '$rawCommand' not found in ${cliDir.name}")
    }

    private fun getCommand(args: List<String>, context: CliFileContext, interactive: Boolean): String? {

        // Return the command if specified
        if (args.isNotEmpty()) {
            return args[0]
        }

        // Print info
        out.printDirectoryInfo(context.info)

        // Select command
        val commands = context.getAllCommands()
        return when {
            interactive -> user.askForCommand(commands)
            else -> {
                out.printCommands(commands)
                null
            }
        }
    }
}