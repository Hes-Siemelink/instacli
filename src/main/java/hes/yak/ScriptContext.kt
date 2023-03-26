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

    var output: JsonNode? = null
    val fileCommands = mutableMapOf<String, ExecuteYayFileAsCommand>()

    fun getCommandHandler(command: String): Command {

        // Variable syntax
        val match = VARIABLE_REGEX.matchEntire(command)
        if (match != null) {
            return VariableCommand(match.groupValues[1])
        }

        // Standard commands
        if (standardCommands.containsKey(command)) {
            return standardCommands[command]!!
        }

        // File commands
        return fileCommands[command] ?: throw ScriptException("Unknown command: ${command}")
    }

    private fun loadCommands(scriptDir: File) {
        for (file in scriptDir.listFiles()) {
            if (file == scriptDir) continue
            if (file.isDirectory) continue
            if (!file.name.endsWith(".yay")) continue

            // Create command name from file name by stripping extension and converting dashes to spaces
            val name = file.name.substring(0, file.name.length - 4).replace('-', ' ')

            fileCommands[name] = ExecuteYayFileAsCommand(file)
        }
    }

    companion object {
        val standardCommands = mapOf(
            "Test case" to TestCase(),
            "Assert equals" to AssertEquals(),
            "Assert that" to AssertThat(),
            "Expected output" to ExpectedOutput(),
            "Input" to Input(),
            "Output" to Output(),
            "Execute yay file" to ExecuteYayFile(),
            "Do" to Do(),
            "For each" to ForEach(),
            "Join" to Join(),
            "As" to AssignOutput()
        )
    }
}