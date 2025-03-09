package instacli.commands.controlflow

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.*

object When : CommandHandler("When", "instacli/control-flow"), ArrayHandler, DelayedResolver {

    override fun execute(data: ArrayNode, context: ScriptContext): JsonNode? {

        for (ifStatement in data) {

            if (ifStatement !is ObjectNode) {
                throw CommandFormatException("Unsupported data type for When statement: ${ifStatement.javaClass.simpleName}.")
            }

            //  'else' matches first, so make sure it is the last entry
            ifStatement.get("else")?.let { elseBranch ->
                return elseBranch.run(context)
            }

            // Execute matching 'if' and exit
            If.evaluate(ifStatement, context)?.let { branch ->
                return branch.run(context)
            }
        }
        return null
    }
}