package yay.cli

import yay.core.ScriptException
import yay.core.YayScript
import java.io.File
import kotlin.system.exitProcess

class Yay(val scriptFile: File) {

    fun runScript() {
        if (!scriptFile.exists()) {
            throw ScriptException("Could not find file: $scriptFile")
        }
        val scriptContext = DirectoryScriptContext(scriptFile)
        scriptContext.addVariables(loadDefaultVariables())

        YayScript.load(scriptFile, scriptContext).run()
    }

}

fun main(args: Array<String>) {
    try {
        val file = File(args[0])
        if (file.isDirectory) {
            throw ScriptException("Can't run yay on a directory: $file")
        } else {
            Yay(file).runScript()
        }
    } catch (e: ScriptException) {
        System.err.println("Yay scripting error")
        System.err.println("\n${e.message}\n")
        e.cause?.printStackTrace()
        
        exitProcess(1)
    }
}

