package hes.yak

import com.fasterxml.jackson.databind.JsonNode

abstract class CommandHandler(val name: String) {

    abstract fun execute(data: JsonNode, context: ScriptContext): JsonNode?
}

interface DelayedVariableResolver

/**
 * This command will process a list of items as a list of commands.
 */
interface ListProcessor