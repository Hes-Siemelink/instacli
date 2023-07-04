package instacli.script.files

import com.fasterxml.jackson.databind.JsonNode
import instacli.cli.CommandLibrary
import instacli.script.commands.AssignVariable
import instacli.script.execution.*
import java.io.File
import java.util.*

const val CLI_FILE_EXTENSION = ".cli"

/**
 * Context for running an Instacli script inside a directory.
 * It will scan the directory for other scripts and expose them as commands.
 */
class ScriptDirectoryContext(
    override val scriptLocation: File,
    override val interactive: Boolean = false
) : ScriptContext {

    private val scriptDir: File
        get() = if (scriptLocation.isDirectory) scriptLocation else scriptLocation.canonicalFile.parentFile

    val info: DirectoryInfo by lazy { DirectoryInfo.load(scriptDir) }
    val name: String
        get() = scriptDir.name

    override val variables = mutableMapOf<String, JsonNode>()

    private val fileCommands: Map<String, CliScriptFile> by lazy { findFileCommands() }
    private val subcommands: Map<String, DirectoryInfo> by lazy { findSubcommands() }

    override fun getCommandHandler(command: String): CommandHandler {

        // Variable syntax
        val match = VARIABLE_REGEX.matchEntire(command)
        if (match != null) {
            return AssignVariable(match.groupValues[1])
        }

        // Standard commands
        if (CommandLibrary.commands.containsKey(command)) {
            return CommandLibrary.commands[command]!!
        }

        // File commands
        return fileCommands[command] ?: throw CliScriptException("Unknown command: $command")
    }

    private fun findFileCommands(): Map<String, CliScriptFile> {

        val commands = mutableMapOf<String, CliScriptFile>()

        // Commands in directory
        for (file in scriptDir.listFiles()!!) {
            addCommand(commands, file)
        }

        // Imported commands
        for (file in info.imports) {
            addCommand(commands, File(scriptDir, file))
        }

        return commands
    }

    private fun addCommand(commands: MutableMap<String, CliScriptFile>, file: File) {
        if (file.isDirectory) return
        if (!file.name.endsWith(CLI_FILE_EXTENSION)) return

        val name = asScriptCommand(file.name)
        commands[name] = CliScriptFile(file)
    }


    fun addVariables(node: JsonNode?) {
        node ?: return

        for (defaultVariable in node.fields()) {
            variables[defaultVariable.key] = defaultVariable.value
        }
    }

    private fun findSubcommands(): Map<String, DirectoryInfo> {
        val subcommands = mutableMapOf<String, DirectoryInfo>()

        for (file in scriptDir.listFiles()!!) {
            if (file.isDirectory && hasCliCommands(file)) {
                subcommands[asCliCommand(file.name)] = DirectoryInfo.load(file)
            }
        }

        return subcommands
    }

    fun getAllCommands(): List<CommandInfo> {
        val commands = mutableListOf<CommandInfo>()
        commands.addAll(fileCommands.values)
        commands.addAll(subcommands.values)

        return commands.sortedBy { it.name }
    }

    fun getCliScriptFile(rawCommand: String): CliScriptFile? {
        val command = asScriptCommand(rawCommand)
        return fileCommands[command]
    }

    fun getSubcommand(rawCommand: String): DirectoryInfo? {
        val command = asCliCommand(rawCommand)
        return subcommands[command]
    }
}

private fun hasCliCommands(dir: File): Boolean {

    // Check for Instacli files
    for (file in dir.listFiles()!!) {
        if (!file.isDirectory && file.name.endsWith(CLI_FILE_EXTENSION)) {
            return true
        }
    }

    // Check for subcommands
    for (file in dir.listFiles()!!) {
        if (file.isDirectory) {
            return hasCliCommands(file)
        }
    }

    return false
}

//
// Command names
//

/**
 * Creates command name from file name by stripping extension and converting dashes to spaces.
 */
fun asScriptCommand(commandName: String): String {
    var command = commandName

    // Strip extension
    if (command.endsWith(CLI_FILE_EXTENSION)) {
        command = command.substring(0, commandName.length - CLI_FILE_EXTENSION.length)
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

    // Strip extension
    if (command.endsWith(CLI_FILE_EXTENSION)) {
        command = command.substring(0, commandName.length - CLI_FILE_EXTENSION.length)
    }

    // Dashes for spaces
    command = command.replace(' ', '-')

    // All lower case
    command = command.lowercase(Locale.getDefault())

    return command
}

