package hes.yay

import com.fasterxml.jackson.databind.JsonNode
import hes.yay.commands.ExecuteYayFileAsCommandHandler
import hes.yay.commands.VariableCommandHandler
import hes.yay.core.*
import java.io.File

class YayScript(
    private val script: List<JsonNode>,
    val context: ScriptContext = DefaultScriptContext()
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
            scriptContext: DefaultScriptContext = DefaultScriptContext()
        ): YayScript {

            val script = Yaml.parse(source)
            scriptContext.scriptLocation = source

            return YayScript(script, scriptContext)
        }

    }
}

class DefaultScriptContext : ScriptContext {

    override val variables = mutableMapOf<String, JsonNode>()
    override var scriptLocation: File? = null
        set(value) {
            field = value
            loadCommands(value!!.parentFile)
        }

    private val fileCommands = mutableMapOf<String, ExecuteYayFileAsCommandHandler>()

    override fun getCommandHandler(command: String): CommandHandler {

        // Variable syntax
        val match = VARIABLE_REGEX.matchEntire(command)
        if (match != null) {
            return VariableCommandHandler(match.groupValues[1])
        }

        // Standard commands
        if (CoreLibrary.commands.containsKey(command)) {
            return CoreLibrary.commands[command]!!
        }

        // File commands
        return fileCommands[command] ?: throw ScriptException("Unknown command: $command")
    }

    private fun loadCommands(scriptDir: File) {
        for (file in scriptDir.listFiles()!!) {
            if (file == scriptDir) continue
            if (file.isDirectory) continue
            if (!file.name.endsWith(".yay")) continue

            // Create command name from file name by stripping extension and converting dashes to spaces
            val name = file.name.substring(0, file.name.length - 4).replace('-', ' ')

            fileCommands[name] = ExecuteYayFileAsCommandHandler(file)
        }
    }
}