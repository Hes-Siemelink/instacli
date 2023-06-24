package yay.cli

import yay.core.ScriptException
import yay.core.YayScript
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
            runYayDirectory(file, args.drop(1))
        } else {
            runYayScript(file)
        }
    } catch (e: CliException) {
        System.err.println(e.message)

        exitProcess(1)
    } catch (e: ScriptException) {
        System.err.println("Yay scripting error")
        System.err.println("\n${e.message}")
        if (e.cause != null) {
            System.err.println()
            e.cause?.printStackTrace()
        }

        exitProcess(1)
    }
}

fun runYayScript(scriptFile: File) {
    val scriptContext = DirectoryScriptContext(scriptFile)
    scriptContext.addVariables(loadDefaultVariables())

    YayScript.load(scriptFile, scriptContext).run()
}

fun runYayDirectory(yayDir: File, args: List<String>) {
    val context = DirectoryScriptContext(yayDir)

    // No Yay scripts in directory
    if (context.fileCommands.isEmpty()) {
        println("No Yay commands in ${yayDir.absolutePath}")
        return
    }

    // No command specified -- list all Yay scripts as commands
    if (args.isEmpty()) {
        println("Available commands:")
        context.fileCommands.keys.sorted().forEach {
            println("  ${asCliCommand(it)}")
        }
        return
    }

    // Check if specified command exists
    val command = asYayCommand(args.get(0))
    if (!context.fileCommands.containsKey(command)) {
        throw CliException("Command '${args.get(0)} 'not found in ${yayDir.name}")
    }

    // Run command
    context.addVariables(loadDefaultVariables())

    val scriptFile = context.fileCommands[command]!!.scriptFile
    YayScript.load(scriptFile, context).run()
}
