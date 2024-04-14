package instacli.commands.variables

import com.fasterxml.jackson.databind.JsonNode
import instacli.language.AnyHandler
import instacli.language.CommandHandler
import instacli.language.ScriptContext

class AssignVariable(private val varName: String) : CommandHandler("\${}", null), AnyHandler {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {

        context.variables[varName] = data

        return null
    }
}