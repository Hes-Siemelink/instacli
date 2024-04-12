package instacli.commands.controlflow

import com.fasterxml.jackson.databind.JsonNode
import instacli.language.AnyHandler
import instacli.language.Break
import instacli.language.CommandHandler
import instacli.language.ScriptContext

object Exit : CommandHandler("Exit", "instacli/control-flow"), AnyHandler {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        throw Break(data)
    }
}