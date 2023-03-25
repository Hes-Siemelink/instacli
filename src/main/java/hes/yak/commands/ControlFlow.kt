package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import hes.yak.Command
import hes.yak.DelayedVariableResolver
import hes.yak.ScriptContext
import hes.yak.YakScript

class Do : Command, DelayedVariableResolver {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        return YakScript(listOf(data), context).run()
        return YakScript(listOf(data), context).run()
    }
}