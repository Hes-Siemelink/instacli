package instacli.cli

import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.ExecuteCliFileAsCommandHandler
import instacli.commands.VariableCommandHandler
import instacli.core.*
import instacli.util.Yaml
import java.io.File

const val CLI_FILE_EXTENSION = ".cli"
val INSTACLI_HOME = File(File(System.getProperty("user.home")), ".instacli")

/**
 * Context for running an Instacli script inside a directory.
 * It will scan the directory for other scripts and expose them as commands.
 */
class ScriptDirectoryContext(override val scriptLocation: File) : ScriptContext {

    override val variables = mutableMapOf<String, JsonNode>()
    val fileCommands = mutableMapOf<String, ExecuteCliFileAsCommandHandler>()
    val subcommands: Map<String, File> by lazy { findSubcommands() }
    val info: DirectoryInfo by lazy { DirectoryInfo.load(scriptDir) }
    val name: String
        get() = scriptDir.name


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
        return fileCommands[command] ?: throw CliScriptException("Unknown command: $command")
    }

    private fun loadCommands() {

        // Commands in directory
        for (file in scriptDir.listFiles()!!) {
            loadCommand(file)
        }

        // Imported commands
        for (file in info.imports) {
            loadCommand(File(scriptDir, file))
        }
    }

    private fun loadCommand(file: File) {
        if (file.isDirectory) return
        if (!file.name.endsWith(CLI_FILE_EXTENSION)) return

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

    fun getInfo(): String {
        return info.summary
    }
}

fun runCliScriptFile(scriptFile: File, context: ScriptContext = ScriptDirectoryContext(scriptFile)) {
    loadCliScriptFile(scriptFile, context).run()
}

fun loadCliScriptFile(scriptFile: File, scriptContext: ScriptContext = ScriptDirectoryContext(scriptFile)): CliScript {
    val script = Yaml.parse(scriptFile)
    return CliScript(script, scriptContext)
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

fun loadDefaultVariables(): JsonNode? {
    return Yaml.readFile(File(INSTACLI_HOME, "default-variables.yaml"))
}