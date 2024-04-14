package instacli.commands.variables

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.*

object As : CommandHandler("As", "instacli/variables"), ValueHandler, DelayedResolver {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {

        if (!context.variables.containsKey(OUTPUT_VARIABLE)) {
            throw CommandFormatException("Can't assign output variable because it is empty.")
        }

        // Support both variable syntax and plain variable name
        val variableName = getVariableName(data.asText())

        context.variables[variableName] = context.variables.getValue(OUTPUT_VARIABLE)

        return null
    }

    private fun getVariableName(text: String): String {
        val singleVariableMatch = VARIABLE_REGEX.matchEntire(text)

        return if (singleVariableMatch != null) {
            singleVariableMatch.groupValues[1]
        } else {
            text
        }
    }
}