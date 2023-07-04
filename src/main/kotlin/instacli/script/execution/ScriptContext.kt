package instacli.script.execution

import com.fasterxml.jackson.databind.JsonNode
import instacli.util.Yaml
import java.io.File

interface ScriptContext {
    val interactive: Boolean
    val variables: MutableMap<String, JsonNode>
    val scriptLocation: File
    fun getCommandHandler(command: String): CommandHandler
}

class VariableScope(override val variables: MutableMap<String, JsonNode>, private val parent: ScriptContext) : ScriptContext by parent {

    override val scriptLocation: File
        get() = parent.scriptLocation

    constructor(data: JsonNode, parent: ScriptContext) :
            this(Yaml.mutableMapOf(data), parent)
}

const val OUTPUT_VARIABLE = "output"
