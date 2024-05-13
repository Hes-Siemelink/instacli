package instacli.commands.userinteraction

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.CommandHandler
import instacli.language.ObjectHandler
import instacli.language.ScriptContext
import instacli.language.ValueHandler
import instacli.language.types.ParameterData
import instacli.util.toDomainObject

/**
 * Asks user through simple text prompt
 */
object Prompt : CommandHandler("Prompt", "instacli/user-interaction"), ValueHandler, ObjectHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode {
        return UserPrompt.prompt(data.textValue())
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val parameterData = data.toDomainObject(ParameterData::class)

        // Only ask if condition is true
        parameterData.conditionValid() || return null

        return parameterData.prompt()
    }
}