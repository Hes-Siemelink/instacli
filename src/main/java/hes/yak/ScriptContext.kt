package hes.yak

import com.fasterxml.jackson.databind.JsonNode
import hes.yak.commands.*
import java.io.File

class ScriptContext {

    val variables = mutableMapOf<String, JsonNode>()
    var scriptLocation: File? = null
        set(value) {
            field = value
            loadCommands(value!!.parentFile)
        }

    private val fileCommands = mutableMapOf<String, ExecuteYayFileAsCommandHandler>()

    fun getCommandHandler(command: String): CommandHandler {

        // Variable syntax
        val match = VARIABLE_REGEX.matchEntire(command)
        if (match != null) {
            return VariableCommandHandler(match.groupValues[1])
        }

        // Standard commands
        if (Core.commands.containsKey(command)) {
            return Core.commands[command]!!
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