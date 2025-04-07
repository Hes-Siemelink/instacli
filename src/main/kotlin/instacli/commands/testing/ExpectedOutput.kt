package instacli.commands.testing

import com.fasterxml.jackson.databind.JsonNode
import instacli.language.AnyHandler
import instacli.language.CommandHandler
import instacli.language.InstacliCommandError
import instacli.language.ScriptContext
import instacli.util.Json

object ExpectedOutput : CommandHandler("Expected output", "instacli/testing"), AnyHandler {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {

        val output = context.output

        if (output != data) {
            val error = Json.newObject()
            error.set<JsonNode>("expected", data)
            error.set<JsonNode>("actual", output)
            throw InstacliCommandError("Output", "Unexpected output.", error)
        }

        return null
    }
}