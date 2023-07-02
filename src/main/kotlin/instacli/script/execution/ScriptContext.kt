package instacli.script.execution

import com.fasterxml.jackson.databind.JsonNode
import java.io.File

interface ScriptContext {
    val variables: MutableMap<String, JsonNode>
    val scriptLocation: File
    fun getCommandHandler(command: String): CommandHandler
}