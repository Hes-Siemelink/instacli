package instacli.cli

import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.ListViewOptions
import com.github.kinquirer.components.promptList
import instacli.core.CliScriptException
import instacli.core.asCliCommand
import instacli.core.asScriptCommand
import java.io.File
import kotlin.system.exitProcess

class CliException(message: String) : Exception(message)

fun main(args: Array<String>) {
    try {
        val options = CliCommandLineOptions(args)

        // First argument should be a valid file
        val file = File(options.args[0])
        if (!file.exists()) {
            throw CliException("Could not find file: ${file.absolutePath}")
        }

        // Run script directly or a command from a directory
        if (file.isDirectory) {
            runDirectory(file, options.args.drop(1), options.interactive)
        } else {
            runFile(file)
        }
    } catch (e: CliException) {
        System.err.println(e.message)

        exitProcess(1)
    } catch (e: CliScriptException) {
        System.err.println("Instacli scripting error")
        System.err.println("\n${e.message}")
        if (e.cause != null) {
            System.err.println()
            e.cause?.printStackTrace()
        }

        exitProcess(1)
    }
}

private fun runFile(scriptFile: File) {
    val scriptContext = ScriptDirectoryContext(scriptFile)
    scriptContext.addVariables(loadDefaultVariables())

    runCliScriptFile(scriptFile, scriptContext)
}

private fun runDirectory(cliDir: File, args: List<String>, interactive: Boolean) {
    val context = ScriptDirectoryContext(cliDir)

    // No Instacli scripts in directory
    if (context.getAllCommands().isEmpty()) {
        println("No Instacli commands in ${cliDir.absolutePath}")
        return
    }

    // Parse command

    val rawCommand = getCommand(args, context, interactive) ?: return

    // Run script
    if (context.fileCommands.containsKey(asScriptCommand(rawCommand))) {
        context.addVariables(loadDefaultVariables())

        val scriptFile = context.fileCommands[asScriptCommand(rawCommand)]!!.scriptFile
        runCliScriptFile(scriptFile, context)
    }
    // Run subcommand
    else if (context.subcommands.containsKey(asCliCommand(rawCommand))) {
        runDirectory(context.subcommands[asCliCommand(rawCommand)]!!, args.drop(1), interactive)
    }
    // Command not found
    else {
        throw CliException("Command '$rawCommand' not found in ${cliDir.name}")
    }
}

fun getCommand(args: List<String>, context: ScriptDirectoryContext, interactive: Boolean): String? {

    // Return the command if specified
    if (args.isNotEmpty()) {
        return args.get(0)
    }

    // Print command info
    if (context.getInfo().isNotEmpty()) {
        println(context.getInfo())
    } else {
        println("${context.name} has several subcommands.")
    }
    println()

    // Select command or print options
    if (interactive) {
        // Ask for the command
        val selectedCommand =  KInquirer.promptList(
                message = "Select a subcommand",
                choices = context.getAllCommands().sorted(),
                viewOptions = ListViewOptions(
                        questionMarkPrefix = "",
                        cursor = " > ",
                        nonCursor = "   ",
                )
        )
        println()
        return selectedCommand
    } else {
        // Print the list of available commands
        println("Available commands:")
        context.getAllCommands().sorted().forEach {
            println("  ${asCliCommand(it)}")
        }
        return null;
    }
}

class CliCommandLineOptions {
    var interactive = false
    val args = mutableListOf<String>()

    constructor(args: Array<String>) {
        this.args.addAll(args.filter { !it.startsWith('-') })
        interactive = args.contains("-i")
    }
}