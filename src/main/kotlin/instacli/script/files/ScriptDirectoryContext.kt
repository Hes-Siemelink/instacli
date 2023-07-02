package instacli.script.files

import com.fasterxml.jackson.databind.JsonNode
import instacli.cli.CommandLibrary
import instacli.script.commands.VariableCommandHandler
import instacli.script.execution.*
import java.io.File

const val CLI_FILE_EXTENSION = ".cli"

/**
 * Context for running an Instacli script inside a directory.
 * It will scan the directory for other scripts and expose them as commands.
 */
class ScriptDirectoryContext(override val scriptLocation: File) : ScriptContext {

    val info: DirectoryInfo by lazy { DirectoryInfo.load(scriptDir) }
    val name: String
        get() = scriptDir.name

    val scriptDir: File
        get() = if (scriptLocation.isDirectory) scriptLocation else scriptLocation.canonicalFile.parentFile

    override val variables = mutableMapOf<String, JsonNode>()
    private val fileCommands: Map<String, CliScriptFile> by lazy { findFileCommands() }
    private val subcommands: Map<String, DirectoryInfo> by lazy { findSubcommands() }

    init {
        findFileCommands()
    }

    override fun getCommandHandler(command: String): CommandHandler {

        // Variable syntax
        val match = VARIABLE_REGEX.matchEntire(command)
        if (match != null) {
            return VariableCommandHandler(match.groupValues[1])
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

