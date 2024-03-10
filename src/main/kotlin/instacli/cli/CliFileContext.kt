package instacli.cli

import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.AssignVariable
import instacli.commands.CommandLibrary
import instacli.script.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.isDirectory
import kotlin.io.path.name

const val CLI_FILE_EXTENSION = ".cli"

/**
 * Context for running an Instacli script inside a directory.
 * It will scan the directory for other scripts and expose them as commands.
 */
class CliFileContext(
    override val cliFile: Path,
    override val variables: MutableMap<String, JsonNode> = mutableMapOf(),
    override val session: MutableMap<String, Any?> = mutableMapOf(),
    override val interactive: Boolean = false,
    override val workingDir: Path = Path.of(".")
) : ScriptContext {

    constructor(cliFile: Path, parent: ScriptContext, variables: MutableMap<String, JsonNode> = mutableMapOf()) : this(
        cliFile,
        variables,
        parent.session,
        parent.interactive
    )

    override fun clone(): ScriptContext {
        return CliFileContext(cliFile, variables.toMutableMap(), session.toMutableMap(), interactive, workingDir)
    }

    override val scriptDir: Path by lazy {
        if (cliFile.isDirectory()) {
            cliFile
        } else {
            cliFile.toAbsolutePath().normalize().parent
        }
    }

    override val output: JsonNode?
        get() = variables[OUTPUT_VARIABLE]
    override var error: InstacliCommandError? = null

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
        throw CliScriptingException("Unknown command: $command")
    }

    private fun findLocalFileCommands(): Map<String, CliFile> {

        val commands = mutableMapOf<String, CliFile>()

        for (file in scriptDir.toFile().listFiles()!!) {
            addCommand(commands, file.toPath())
        }

        return commands
    }

    private fun findImportedCommands(): Map<String, CliFile> {

        val commands = mutableMapOf<String, CliFile>()

        for (cliFile in info.imports) {
            addCommand(commands, scriptDir.resolve(cliFile))
        }

        return commands
    }

    private fun addCommand(commands: MutableMap<String, CliFile>, file: Path) {
        if (file.isDirectory()) return
        if (!file.name.endsWith(CLI_FILE_EXTENSION)) return

        val name = asScriptCommand(file.name)
        commands[name] = CliFile(file)
    }

    private fun findSubcommands(): Map<String, DirectoryInfo> {
        val subcommands = mutableMapOf<String, DirectoryInfo>()

        Files.list(scriptDir)
            .filter { it.isDirectory() && it.name != "tests" && it.hasCliCommands() }
            .forEach { dir ->
                subcommands[asCliCommand(dir.name)] = DirectoryInfo.load(dir)
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

private fun Path.hasCliCommands(): Boolean {
    return Files.walk(this).anyMatch() { file ->
        !file.isDirectory() && file.name.endsWith(CLI_FILE_EXTENSION)
    }
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
