package instacli.commands.controlflow

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.commands.parseCondition
import instacli.language.*

object If : CommandHandler("If", "instacli/control-flow"), ObjectHandler, DelayedResolver {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val branch = evaluateIfStatement(data, context) ?: return null

        return branch.runScript(context)
    }

    fun evaluateIfStatement(data: ObjectNode, context: ScriptContext): JsonNode? {
        val thenBranch = data.remove("then") ?: throw CommandFormatException("Command 'If' needs a 'then' parameter.")
        val elseBranch: JsonNode? = data.remove("else")

        val condition = parseCondition(data.resolve(context))
        return if (condition.isTrue()) {
            thenBranch
        } else {
            elseBranch
        }
    }
}

