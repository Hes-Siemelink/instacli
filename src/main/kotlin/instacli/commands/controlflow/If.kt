package instacli.commands.controlflow

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.commands.toCondition
import instacli.language.*

object If : CommandHandler("If", "instacli/control-flow"), ObjectHandler, DelayedResolver {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val branch = evaluate(data, context)

        return branch?.runScript(context)
    }

    fun evaluate(data: ObjectNode, context: ScriptContext): JsonNode? {

        val thenBranch = data.remove("then") ?: throw CommandFormatException("Expected field 'then'.")
        val elseBranch: JsonNode? = data.remove("else")

        val condition = data.resolve(context).toCondition()

        return if (condition.isTrue()) {
            thenBranch
        } else {
            elseBranch
        }
    }
}

