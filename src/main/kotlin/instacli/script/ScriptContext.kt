package instacli.script

import com.fasterxml.jackson.databind.JsonNode
import instacli.commands.Connections
import java.io.File

interface ScriptContext {
    val interactive: Boolean
    val variables: MutableMap<String, JsonNode>
    val session: MutableMap<String, JsonNode>
    val connections: Connections
    val cliFile: File

    fun getCommandHandler(command: String): CommandHandler
    fun getScriptDir(): File = cliFile.parentFile
}

const val OUTPUT_VARIABLE = "output"
