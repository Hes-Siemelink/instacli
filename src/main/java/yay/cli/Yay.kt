package yay.cli

import com.fasterxml.jackson.databind.JsonNode
import yay.core.ScriptException
import yay.core.Yaml
import java.io.File
import kotlin.system.exitProcess

val YAY_DIR = File(File(System.getProperty("user.home")), ".yay")

class Yay(val scriptFile: File) {

    fun runScript() {
        if (!scriptFile.exists()) {
            throw ScriptException("Could not find file: ${scriptFile}")
        }
        val scriptContext = DefaultScriptContext(scriptFile)
        scriptContext.addVariables(loadDefaultVariables())

        YayScript.load(scriptFile, scriptContext).run()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                Yay(File(args[0])).runScript()
            } catch (e: ScriptException) {
                System.err.println("Yay scripting error")
                System.err.println("\n${e.message}\n")
                e.cause?.printStackTrace()
                exitProcess(1)
            }
        }
    }
}

fun loadDefaultVariables(): JsonNode? {
    return Yaml.readFile(File(YAY_DIR, "default-variables.yaml"))
}
