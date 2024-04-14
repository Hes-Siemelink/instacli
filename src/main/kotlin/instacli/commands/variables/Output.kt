package instacli.commands.variables

import com.fasterxml.jackson.databind.JsonNode
import instacli.language.AnyHandler
import instacli.language.CommandHandler
import instacli.language.ScriptContext

/**
 * Returns the input as output.
 */
object Output : CommandHandler("Output", "instacli/variables"), AnyHandler {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode {
        return data
    }
}
