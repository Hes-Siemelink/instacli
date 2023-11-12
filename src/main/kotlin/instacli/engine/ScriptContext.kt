package instacli.engine

import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.Connections
import java.io.File

interface ScriptContext {
    val interactive: Boolean
    val variables: MutableMap<String, JsonNode>
    val session: MutableMap<String, JsonNode>
    val connections: Connections
    val scriptLocation: File

    fun getCommandHandler(command: String): CommandHandler
}

const val OUTPUT_VARIABLE = "output"
