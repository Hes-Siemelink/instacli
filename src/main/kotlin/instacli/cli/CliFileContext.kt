package instacli.cli

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import instacli.commands.AssignVariable
import instacli.commands.Connections
import instacli.engine.*
import java.io.File
import java.util.*

const val CLI_FILE_EXTENSION = ".cli"

/**
 * Context for running an Instacli script inside a directory.
 * It will scan the directory for other scripts and expose them as commands.
 */
class CliFileContext(
    override val cliFile: File,
    override val variables: MutableMap<String, JsonNode> = mutableMapOf<String, JsonNode>(),
    override val session: MutableMap<String, JsonNode> = mutableMapOf<String, JsonNode>(),
    override var connections: Connections = Connections(),
    override val interactive: Boolean = false
) : ScriptContext {

    constructor(cliFile: File, parent: ScriptContext, variables: MutableMap<String, JsonNode> = mutableMapOf()) : this(
        cliFile,
        variables,
        parent.session,
        parent.connections,
        parent.interactive
    )

    private val scriptDir: File
        get() = if (cliFile.isDirectory) cliFile else cliFile.canonicalFile.parentFile

    val info: DirectoryInfo by lazy { DirectoryInfo.load(scriptDir) }
    val name: String
        get() = scriptDir.name

    private val localFileCommands: Map<String, CliFile> by lazy { findLocalFileCommands() }
    private val importedFileCommands: Map<String, CliFile> by lazy { findImportedCommands() }
    private val subdirectoryCommands: Map<String, DirectoryInfo> by lazy { findSubcommands() }


    override fun getCommandHandler(command: String): CommandHandler {

        // Variable syntax
        val match = VARIABLE_REGEX.matchEntire(command)
        if (match != null) {
            return AssignVariable(match.groupValues[1])
        }

        // Standard commands
        CommandLibrary.commands[command]?.let { handler ->
            return handler
        }

        // File commands
        localFileCommands[command]?.let { handler ->
            return handler
        }

        // Imported commands
        importedFileCommands[command]?.let { handler ->
            return handler
        }

        // No handler found for command
        throw CliScriptException("Unknown command: $command")
    }

    private fun findLocalFileCommands(): Map<String, CliFile> {

        val commands = mutableMapOf<String, CliFile>()

        for (file in scriptDir.listFiles()!!) {
            addCommand(commands, file)
        }

        return commands
    }

    private fun findImportedCommands(): Map<String, CliFile> {

        val commands = mutableMapOf<String, CliFile>()

        for (cliFile in info.imports) {
            addCommand(commands, File(scriptDir, cliFile))
        }

        return commands
    }

    private fun addCommand(commands: MutableMap<String, CliFile>, file: File) {
        if (file.isDirectory) return
        if (!file.name.endsWith(CLI_FILE_EXTENSION)) return

        val name = asScriptCommand(file.name)
        commands[name] = CliFile(file)
    }

    fun addVariables(vars: Map<String, String>) {
        for (variable in vars) {
            variables[variable.key] = TextNode(variable.value)
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
        commands.addAll(localFileCommands.values)
        commands.addAll(subdirectoryCommands.values)

        return commands.sortedBy { it.name }
    }

    fun getCliScriptFile(rawCommand: String): CliFile? {
        val command = asScriptCommand(rawCommand)
        return localFileCommands[command]
    }

    fun getSubcommand(rawCommand: String): DirectoryInfo? {
        val command = asCliCommand(rawCommand)
        return subdirectoryCommands[command]
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
