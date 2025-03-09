package instacli.commands.controlflow

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.commands.toCondition
import instacli.language.*

object If : CommandHandler("If", "instacli/control-flow"), ObjectHandler, DelayedResolver {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val branch = evaluate(data, context)

        return branch?.run(context)
    }

    fun evaluate(data: ObjectNode, context: ScriptContext): JsonNode? {

        val workingCopy = data.deepCopy()
        val thenBranch = workingCopy.remove("then") ?: throw CommandFormatException("Expected field 'then'.")
        val elseBranch: JsonNode? = workingCopy.remove("else")

        val condition = workingCopy.resolve(context).toCondition()

        return if (condition.isTrue()) {
            thenBranch
        } else {
            elseBranch
        }
    }
}

