package hes.yak

import com.fasterxml.jackson.databind.JsonNode

interface Command {
    fun execute(data: JsonNode, context: ScriptContext): JsonNode?
}