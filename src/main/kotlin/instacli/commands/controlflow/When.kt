package instacli.commands.controlflow

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.commands.controlflow.If.evaluateIfStatement
import instacli.language.*

object When : CommandHandler("When", "instacli/control-flow"), ArrayHandler, DelayedResolver {
    override fun execute(data: ArrayNode, context: ScriptContext): JsonNode? {
        for (ifStatement in data) {

            if (ifStatement !is ObjectNode) {
                throw CommandFormatException("Unsupported data type for if statement: ${ifStatement.javaClass.simpleName}.")
            }

            // Single 'else'
            if (ifStatement.size() == 1) {
                ifStatement.get("else")?.let {
                    return it.runScript(context)
                }
            }

            // Regular 'if'
            val branch = evaluateIfStatement(ifStatement, context) ?: continue
            return branch.runScript(context)
        }
        return null
    }
}