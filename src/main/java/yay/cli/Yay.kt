package yay.cli

import yay.core.ScriptException
import yay.core.YayScript
import java.io.File
import kotlin.system.exitProcess

class CliException(message: String) : Exception(message)

fun main(args: Array<String>) {
    try {
        val file = File(args[0])
        if (!file.exists()) {
            throw CliException("Could not find file: ${file.absolutePath}")
        }

        if (file.isDirectory) {
            runYayDirectory(file)
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

fun runYayDirectory(yayDir: File) {
    val scriptContext = DirectoryScriptContext(yayDir)

    if (scriptContext.fileCommands.isEmpty()) {
        println("No Yay commands in ${yayDir.absolutePath}")
    } else {
        println("Available commands:")
        scriptContext.fileCommands.keys.sorted().forEach {
            println("  ${asCliCommand(it)}")
        }
    }
}

