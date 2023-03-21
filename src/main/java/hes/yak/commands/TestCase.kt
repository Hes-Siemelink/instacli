package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import hes.yak.Command

class TestCase : Command {

    override fun execute(node: JsonNode) {
        System.out.println("Test case: ${node.asText()}")
    }
}