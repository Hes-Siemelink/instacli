package instacli.cli

import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.ExecuteCliFileAsCommandHandler
import instacli.commands.VariableCommandHandler
import instacli.core.*
import java.io.File
import java.util.*

const val CLI_EXTENSION = ".cli"
val INSTACLI_HOME = File(File(System.getProperty("user.home")), ".instacli")

/**
 * Context for running an Instacli script inside a directory.
 * It will scan the directory for other scripts and expose them as commands.
 */
class DirectoryScriptContext(override val scriptLocation: File) : ScriptContext {

    override val variables = mutableMapOf<String, JsonNode>()
    val fileCommands = mutableMapOf<String, ExecuteCliFileAsCommandHandler>()
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
        if (instacli.cli.CoreLibrary.commands.containsKey(command)) {
            return instacli.cli.CoreLibrary.commands[command]!!
        }

        // File commands
        return fileCommands[command] ?: throw CliScriptException("Unknown command: $command")
    }

    private fun loadCommands() {

        for (file in scriptDir.listFiles()!!) {
            loadCommand(file)
        }

        val info = InstacliInfo.load(scriptDir)
        for (file in info.imports) {
            loadCommand(File(scriptDir, file))
        }
    }

    private fun loadCommand(file: File) {
        if (file.isDirectory) return
        if (!file.name.endsWith(CLI_EXTENSION)) return

        val name = asScriptCommand(file.name)

        fileCommands[name] = ExecuteCliFileAsCommandHandler(file)
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
                if (hasCliCommands(file)) {
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

private fun hasCliCommands(dir: File): Boolean {

    // Check for Instacli files
    for (file in dir.listFiles()!!) {
        if (!file.isDirectory && file.name.endsWith(CLI_EXTENSION)) {
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

/**
 * Creates command name from file name by stripping extension and converting dashes to spaces.
 */
fun asScriptCommand(commandName: String): String {
    var command = commandName

    // Strip extension
    if (command.endsWith(CLI_EXTENSION)) {
        command = command.substring(0, commandName.length - CLI_EXTENSION.length)
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
    if (command.endsWith(CLI_EXTENSION)) {
        command = command.substring(0, commandName.length - CLI_EXTENSION.length)
    }

    // Dashes for spaces
    command = command.replace(' ', '-')

    // All lower case
    command = command.lowercase(Locale.getDefault())

    return command
}

fun loadDefaultVariables(): JsonNode? {
    return Yaml.readFile(File(INSTACLI_HOME, "default-variables.yaml"))
}