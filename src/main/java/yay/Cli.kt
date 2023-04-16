package yay

import yay.core.ScriptException
import java.io.File

object Cli {

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            YayScript.run(File(args[0]))
        } catch (e: ScriptException) {
            System.err.println(e)
            System.exit(1)
        }
    }
}
