package instacli.script

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import instacli.util.objectNode
import java.nio.file.Path

interface ScriptContext {
    val interactive: Boolean
    val variables: MutableMap<String, JsonNode>
    val session: MutableMap<String, Any?>
    val cliFile: Path
    val scriptDir: Path
    val workingDir: Path
    val output: JsonNode?
    var error: InstacliErrorCommand?

    fun getCommandHandler(command: String): CommandHandler
    fun clone(): ScriptContext
}

fun ScriptContext.addInputVariables(vars: Map<String, String>) {
    val input = objectNode()
    for (variable in vars) {
        input.set<JsonNode>(variable.key, TextNode(variable.value))
    }
    variables[INPUT_VARIABLE] = input
}

const val INPUT_VARIABLE = "input"
const val OUTPUT_VARIABLE = "output"
