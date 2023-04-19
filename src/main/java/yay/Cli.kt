package yay

import com.fasterxml.jackson.databind.JsonNode
import yay.core.ScriptException
import yay.core.Yaml
import java.io.File
import kotlin.system.exitProcess

val YAY_DIR = File(File(System.getProperty("user.home")), ".yay")

class Cli(val scriptFile: File) {

    fun runScript() {
        val scriptContext = DefaultScriptContext(scriptFile)
        scriptContext.addVariables(loadDefaultVariables())

        YayScript.load(scriptFile, scriptContext).run()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                Cli(File(args[0])).runScript()
            } catch (e: ScriptException) {
                System.err.println(e)
                exitProcess(1)
            }
        }
    }
}

fun loadDefaultVariables(): JsonNode? {
    return Yaml.readFile(File(YAY_DIR, "default-variables.yaml"))
}
