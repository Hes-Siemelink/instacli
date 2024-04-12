package instacli.commands.testing

import com.fasterxml.jackson.databind.JsonNode
import instacli.language.AnyHandler
import instacli.language.CommandHandler
import instacli.language.ScriptContext

object ExpectedOutput : CommandHandler("Expected output", "instacli/testing"), AnyHandler {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        val output = context.output
        if (output == null || output != data) {
            throw AssertionError("Unexpected output.\nExpected: ${data}\nOutput:   $output")
        }
        return null
    }
}