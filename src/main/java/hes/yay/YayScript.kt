package hes.yay

import com.fasterxml.jackson.databind.JsonNode
import hes.yay.core.ScriptContext
import hes.yay.core.Yaml
import hes.yay.core.runScript
import hes.yay.core.toCommands
import java.io.File

class YayScript(
    private val script: List<JsonNode>,
    val context: ScriptContext = ScriptContext()
) {

    fun run(): JsonNode? {
        val statements = script.map { scriptNode -> toCommands(scriptNode) }.flatten()
        return runScript(statements, context)
    }

    companion object {

        fun run(script: File) {
            load(script).run()
        }

        fun load(
            source: File,
            scriptContext: ScriptContext = ScriptContext()
        ): YayScript {

            val script = Yaml.parse(source)
            scriptContext.scriptLocation = source

            return YayScript(script, scriptContext)
        }

    }
}

