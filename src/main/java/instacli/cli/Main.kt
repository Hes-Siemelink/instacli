package instacli.cli

import instacli.core.CliScript
import instacli.core.CliScriptException
import java.io.File
import kotlin.system.exitProcess

class CliException(message: String) : Exception(message)

fun main(args: Array<String>) {
    try {

        // First argument should be a valid file
        val file = File(args[0])
        if (!file.exists()) {
            throw CliException("Could not find file: ${file.absolutePath}")
        }

        // Run script directly or a command from a directory
        if (file.isDirectory) {
            runCliDirectory(file, args.drop(1))
        } else {
            runCliScript(file)
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

fun runCliScript(scriptFile: File) {
    val scriptContext = DirectoryScriptContext(scriptFile)
    scriptContext.addVariables(loadDefaultVariables())

    CliScript.load(scriptFile, scriptContext).run()
}

fun runCliDirectory(cliDir: File, args: List<String>) {
    val context = DirectoryScriptContext(cliDir)

    // No Instacli scripts in directory
    if (context.getAllCommands().isEmpty()) {
        println("No Instacli commands in ${cliDir.absolutePath}")
        return
    }

    // No command specified -- list all Instacli scripts as commands
    if (args.isEmpty()) {
        println("Available commands:")
        context.getAllCommands().sorted().forEach {
            println("  ${asCliCommand(it)}")
        }
        return
    }

    // Run command
    val rawCommand = args.get(0)

    // Local script
    if (context.fileCommands.containsKey(asScriptCommand(rawCommand))) {
        context.addVariables(loadDefaultVariables())

        val scriptFile = context.fileCommands[asScriptCommand(rawCommand)]!!.scriptFile
        CliScript.load(scriptFile, context).run()
    }
    // Subcommand
    else if (context.subcommands.containsKey(asCliCommand(rawCommand))) {
        runCliDirectory(context.subcommands[asCliCommand(rawCommand)]!!, args.drop(1))
    }
    // Command not found
    else {
        throw CliException("Command '$rawCommand' not found in ${cliDir.name}")
    }
}
