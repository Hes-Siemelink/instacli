package instacli.script

import com.fasterxml.jackson.databind.JsonNode
import java.nio.file.Path

interface ScriptContext {
    val interactive: Boolean
    val variables: MutableMap<String, JsonNode>
    val session: MutableMap<String, Any?>
    val cliFile: Path
    val scriptDir: Path
    val workingDir: Path

    fun getCommandHandler(command: String): CommandHandler
}

const val INPUT_VARIABLE = "input"
const val OUTPUT_VARIABLE = "output"
