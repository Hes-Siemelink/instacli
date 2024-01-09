package instacli.script

import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.Connections
import java.nio.file.Path

interface ScriptContext {
    val interactive: Boolean
    val variables: MutableMap<String, JsonNode>
    val session: MutableMap<String, JsonNode>
    val connections: Connections
    val cliFile: Path
    val scriptDir: Path
    val workingDir: Path

    fun getCommandHandler(command: String): CommandHandler
}

const val OUTPUT_VARIABLE = "out"
