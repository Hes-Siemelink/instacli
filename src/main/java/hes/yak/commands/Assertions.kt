package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import hes.yak.Command

class AssertEquals : Command {
    override fun execute(node: JsonNode) {
        val actual = node.get("actual")
        val expected = node.get("expected")

        if (!actual.equals(expected)) {
            throw AssertionError("Not equal:\n  Expected: $expected\n  Actual:   $actual")
        }
    }

}