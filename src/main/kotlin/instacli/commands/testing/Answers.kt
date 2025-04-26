package instacli.commands.testing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.CommandHandler
import instacli.language.ObjectHandler
import instacli.language.ScriptContext

/**
 * Records answers to be replayed in test cases for user input commands.
 */
object Answers : CommandHandler("Answers", "instacli/testing"), ObjectHandler {

    val recordedAnswers = mutableMapOf<String, JsonNode>()

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        data.fields().forEach {
            recordedAnswers[it.key] = it.value
        }

        return null
    }
}
