package instacli.commands.userinteraction

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.CommandHandler
import instacli.language.InstacliCommandError
import instacli.language.ScriptContext
import instacli.language.ValueHandler
import instacli.language.types.ParameterData
import instacli.util.toDisplayYaml

/**
 * Asks user to confirm a question
 */
object Confirm : CommandHandler("Confirm", "instacli/user-interaction"), ValueHandler {

    val yes = TextNode("Yes")
    val no = TextNode("No")

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode {

        val question = data.toDisplayYaml()

        val confirmationDialog = ParameterData(
            description = question,
            enum = listOf(yes, no)
        )

        val answer = confirmationDialog.prompt()

        if (answer == no) {
            throw InstacliCommandError("No confirmation -- action canceled.")
        }

        return answer
    }
}