package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import hes.yak.*

class Do : Command, DelayedVariableResolver, ListProcessor {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        return YakScript(listOf(data), context).run()
    }
}