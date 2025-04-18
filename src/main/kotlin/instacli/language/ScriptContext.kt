package instacli.language

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.types.TypeRegistry
import instacli.util.Json
import java.nio.file.Path

interface ScriptContext {
    val interactive: Boolean
    val variables: MutableMap<String, JsonNode>
    val session: MutableMap<String, Any?>
    val cliFile: Path
    val scriptDir: Path
    val workingDir: Path
    val tempDir: Path
    val output: JsonNode?
    var error: InstacliCommandError?
    val types: TypeRegistry

    fun getCommandHandler(command: String): CommandHandler
    fun clone(): ScriptContext
}

fun ScriptContext.getInputVariables(): ObjectNode {
    return variables.getOrPut(INPUT_VARIABLE) { Json.newObject() } as ObjectNode
}

const val INPUT_VARIABLE = "input"
const val OUTPUT_VARIABLE = "output"
const val SCRIPT_TEMP_DIR_VARIABLE = "SCRIPT_TEMP_DIR"
