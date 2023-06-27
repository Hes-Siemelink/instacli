package instacli.core

import com.fasterxml.jackson.databind.JsonNode
import instacli.cli.DirectoryScriptContext
import java.io.File

class CliScript(
    private val script: List<JsonNode>,
    val context: ScriptContext
) {

    fun run(): JsonNode? {
        val statements = script.map { scriptNode -> toCommands(scriptNode) }.flatten()
        return runScript(statements, context)
    }

    companion object {

        fun run(script: File) {
            load(script, DirectoryScriptContext(script)).run()
        }

        fun load(source: File, scriptContext: ScriptContext): CliScript {

            val script = Yaml.parse(source)

            return CliScript(script, scriptContext)
        }

    }
}