package yay.cli

import com.fasterxml.jackson.databind.JsonNode
import yay.commands.ExecuteYayFileAsCommandHandler
import yay.commands.VariableCommandHandler
import yay.core.*
import java.io.File
import java.util.*

const val YAY_EXTENSION = ".yay"
val YAY_DIR = File(File(System.getProperty("user.home")), YAY_EXTENSION)

/**
 * Context for running a Yay script inside a directory.
 * It will scan the directory for other scripts and expose them as commands.
 */
class DirectoryScriptContext(override val scriptLocation: File) : ScriptContext {

    override val variables = mutableMapOf<String, JsonNode>()
    val fileCommands = mutableMapOf<String, ExecuteYayFileAsCommandHandler>()
    val subcommands: Map<String, File> by lazy { findSubcommands() }

    val scriptDir: File
        get() = if (scriptLocation.isDirectory) scriptLocation else scriptLocation.canonicalFile.parentFile


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

        for (file in scriptDir.listFiles()!!) {
            if (file == scriptDir) continue
            if (file.isDirectory) continue
            if (!file.name.endsWith(YAY_EXTENSION)) continue

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

    private fun findSubcommands(): Map<String, File> {
        val subcommands = mutableMapOf<String, File>()

        for (file in scriptDir.listFiles()!!) {
            if (file.isDirectory) {
                if (hasYayCommands(file)) {
                    subcommands.put(asCliCommand(file.name), file)
                }
            }
        }

        return subcommands
    }

    fun getAllCommands(): List<String> {
        val commands = mutableListOf<String>()
        commands.addAll(fileCommands.keys)
        commands.addAll(subcommands.keys)

        return commands
    }
}

private fun hasYayCommands(dir: File): Boolean {

    // Check for Yay files
    for (file in dir.listFiles()!!) {
        if (!file.isDirectory && file.name.endsWith(YAY_EXTENSION)) {
            return true
        }
    }

    // Check for subcommands
    for (file in dir.listFiles()!!) {
        if (file.isDirectory) {
            return hasYayCommands(file)
        }
    }

    return false
}

fun asYayCommand(commandName: String): String {
    var command = commandName

    // Strip .yay
    if (command.endsWith(YAY_EXTENSION)) {
        command = command.substring(0, commandName.length - YAY_EXTENSION.length)
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
    if (command.endsWith(YAY_EXTENSION)) {
        command = command.substring(0, commandName.length - YAY_EXTENSION.length)
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