package instacli.language

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
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
    val output: JsonNode?
    var error: InstacliCommandError?
    val types: TypeRegistry

    fun getCommandHandler(command: String): CommandHandler
    fun clone(): ScriptContext
}

fun ScriptContext.addInputVariables(vars: Map<String, String>) {
    val input = Json.newObject()
    for (variable in vars) {
        input.set<JsonNode>(variable.key, TextNode(variable.value))
    }
    variables[INPUT_VARIABLE] = input
}

const val INPUT_VARIABLE = "input"
const val OUTPUT_VARIABLE = "output"
