package yay.cli

import com.fasterxml.jackson.databind.JsonNode
import yay.commands.ExecuteYayFileAsCommandHandler
import yay.commands.VariableCommandHandler
import yay.core.*
import java.io.File
import java.util.*

val YAY_DIR = File(File(System.getProperty("user.home")), ".yay")

class DirectoryScriptContext(override val scriptLocation: File) : ScriptContext {

    override val variables = mutableMapOf<String, JsonNode>()
    val fileCommands = mutableMapOf<String, ExecuteYayFileAsCommandHandler>()

    init {
        loadCommands()
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

    private fun loadCommands() {
        val scriptDir = if (scriptLocation.isDirectory) {
            scriptLocation
        } else {
            scriptLocation.canonicalFile.parentFile
        }

        for (file in scriptDir.listFiles()!!) {
            if (file == scriptDir) continue
            if (file.isDirectory) continue
            if (!file.name.endsWith(".yay")) continue

            // Create command name from file name by stripping extension and converting dashes to spaces
            val name = asYayCommand(file.name)

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

fun asYayCommand(commandName: String): String {
    var command = commandName

    // Strip .yay
    if (command.endsWith(".yay")) {
        command = command.substring(0, commandName.length - 4)
    }

    // Spaces for dashes
    command = command.replace('-', ' ')

    // Start with a capital
    command =
        command.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    return command
}

fun asCliCommand(commandName: String): String {
    var command = commandName

    // Strip .yay
    if (command.endsWith(".yay")) {
        command = command.substring(0, commandName.length - 4)
    }

    // Dashes for spaces
    command = command.replace(' ', '-')

    // All lower case
    command = command.lowercase(Locale.getDefault())

    return command
}

fun loadDefaultVariables(): JsonNode? {
    return Yaml.readFile(File(YAY_DIR, "default-variables.yaml"))
}