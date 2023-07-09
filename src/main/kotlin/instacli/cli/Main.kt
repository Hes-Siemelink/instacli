package instacli.cli

import com.fasterxml.jackson.databind.JsonNode
import instacli.script.execution.CliScriptException
import instacli.script.files.CliScriptFile
import instacli.script.files.ScriptDirectoryContext
import instacli.util.Yaml
import instacli.util.objectNode
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
    } catch (e: CliScriptException) {
        val cause = e.cause?.message ?: ""
        System.err.println("Instacli scripting error\n\n  $cause")
        System.err.println("\n${e.message}")

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

        // First argument should be a valid file
        val file = File(workingDir, options.commands[0])
        if (!file.exists()) {
            throw CliException("Could not find file: ${file.absolutePath}")
        }

        // Run script directly or a command from a directory
        val context = ScriptDirectoryContext(file, options)
        if (file.isDirectory) {
            runDirectory(file, options.commands.drop(1), context)
        } else {
            runFile(CliScriptFile(file), context)
        }
    }

    private fun runFile(script: CliScriptFile, context: ScriptDirectoryContext) {
        context.addVariables(loadDefaultVariables())
        context.addVariables(context.options.commandParameters)

        when {
            context.options.help -> out.printScriptInfo(script.cliScript)
            else -> script.run(context)
        }
    }

    private fun runDirectory(cliDir: File, args: List<String>, context: ScriptDirectoryContext) {

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
            runFile(script, context)
            return
        }

        // Run subcommand
        val subcommand = context.getSubcommand(rawCommand)
        if (subcommand != null) {
            runDirectory(subcommand.dir, args.drop(1), ScriptDirectoryContext(subcommand.dir, context.options))
            return
        }

        // Command not found
        throw CliException("Command '$rawCommand' not found in ${cliDir.name}")
    }

    private fun getCommand(args: List<String>, context: ScriptDirectoryContext, interactive: Boolean): String? {

        // Return the command if specified
        if (args.isNotEmpty()) {
            return args[0]
        }

        // Print info
        if (context.info.description.isNotEmpty()) {
            println(context.info.description.trim())
        } else {
            println("${context.name} has several subcommands.")
        }
        println()


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


private fun loadDefaultVariables(): JsonNode {
    return Yaml.readFile(File(INSTACLI_HOME, "default-variables.yaml")) ?: objectNode()
}