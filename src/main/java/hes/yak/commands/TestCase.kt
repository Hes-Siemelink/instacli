package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import hes.yak.Command
import hes.yak.ScriptContext

class TestCase : Command {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        System.out.println("Test case: ${data.asText()}")
        return null
    }
}