package instacli.cli

import com.fasterxml.jackson.databind.JsonNode
import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.ListViewOptions
import com.github.kinquirer.components.promptListObject
import com.github.kinquirer.core.Choice
import instacli.script.execution.CliScriptException
import instacli.script.execution.CommandInfo
import instacli.script.execution.asCliCommand
import instacli.script.execution.asScriptCommand
import instacli.script.files.ScriptDirectoryContext
import instacli.script.files.runCliScriptFile
import instacli.util.Yaml
import java.io.File
import kotlin.system.exitProcess

val INSTACLI_HOME = File(File(System.getProperty("user.home")), ".instacli")

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
        runDirectory(context.subcommands[asCliCommand(rawCommand)]!!.dir, args.drop(1), interactive)
    }
    // Command not found
    else {
        throw CliException("Command '$rawCommand' not found in ${cliDir.name}")
    }
}

private fun getCommand(args: List<String>, context: ScriptDirectoryContext, interactive: Boolean): String? {

    // Return the command if specified
    if (args.isNotEmpty()) {
        return args[0]
    }

    // Print info
    if (context.getInfo().isNotEmpty()) {
        println(context.getInfo().trim())
    } else {
        println("${context.name} has several subcommands.")
    }
    println()


    // Select command
    val commands = context.getAllCommands()
    return when {
        interactive -> {
            askForCommand(commands)
        }
        else -> {
            printCommands(commands)
            null
        }
    }
}

private fun askForCommand(commands: List<CommandInfo>): String {
    val width = commands.maxOf { it.name.length }
    val selectedCommand = KInquirer.promptListObject(
            message = "Available commands:",
            choices = commands.map { Choice(it.infoString(width), it.name) },
            viewOptions = ListViewOptions(
                    questionMarkPrefix = "*",
                    cursor = " > ",
                    nonCursor = "   ",
            )
    )
    println("---")
    return selectedCommand
}

private fun printCommands(commands: List<CommandInfo>) {
    println("Available commands:")

    val width = commands.maxOf { it.name.length }
    commands.forEach {
        println("  ${it.infoString(width)}")
    }
}

private fun loadDefaultVariables(): JsonNode? {
    return Yaml.readFile(File(INSTACLI_HOME, "default-variables.yaml"))
}