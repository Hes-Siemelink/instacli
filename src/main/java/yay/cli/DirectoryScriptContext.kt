package yay.cli

import com.fasterxml.jackson.databind.JsonNode
import yay.commands.ExecuteYayFileAsCommandHandler
import yay.commands.VariableCommandHandler
import yay.core.*
import java.io.File

val YAY_DIR = File(File(System.getProperty("user.home")), ".yay")

class DirectoryScriptContext(override val scriptLocation: File) : ScriptContext {

    override val variables = mutableMapOf<String, JsonNode>()
    private val fileCommands = mutableMapOf<String, ExecuteYayFileAsCommandHandler>()

    init {
        loadCommands(scriptLocation.canonicalFile.parentFile)
    }

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

    fun addVariables(node: JsonNode?) {
        node ?: return

        for (defaultVariable in node.fields()) {
            variables[defaultVariable.key] = defaultVariable.value
        }
    }
}

fun loadDefaultVariables(): JsonNode? {
    return Yaml.readFile(File(YAY_DIR, "default-variables.yaml"))
}