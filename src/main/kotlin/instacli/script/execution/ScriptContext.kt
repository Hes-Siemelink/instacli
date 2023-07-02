package instacli.script.execution

import com.fasterxml.jackson.databind.JsonNode
import java.io.File

interface ScriptContext {
    val variables: MutableMap<String, JsonNode>
    val scriptLocation: File
    fun getCommandHandler(command: String): CommandHandler
}

class VariableScopeContext(override val variables: MutableMap<String, JsonNode>, val parent: ScriptContext) : ScriptContext {
    override val scriptLocation: File
        get() = parent.scriptLocation

    override fun getCommandHandler(command: String): CommandHandler {
        return parent.getCommandHandler(command)
    }
}