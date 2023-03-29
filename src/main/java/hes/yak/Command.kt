package hes.yak

import com.fasterxml.jackson.databind.JsonNode
import hes.yak.commands.*

abstract class CommandHandler(val name: String) {

    abstract fun execute(data: JsonNode, context: ScriptContext): JsonNode?
}

interface DelayedVariableResolver

/**
 * This command will process a list of items as a list of commands.
 */
interface ListProcessor

object Core {
    val commands = commandMap(
        TestCase(),
        AssertEquals(),
        AssertThat(),
        ExpectedOutput(),
        Input(),
        Output(),
        ExecuteYayFile(),
        Do(),
        ForEach(),
        Join(),
        AssignOutput(),
        Merge(),
        Print(),
        ReadFile(),
        ApplyVariables(),
        Repeat(),
        If(),
        IfAny()
    )

    private fun commandMap(vararg commands: CommandHandler): Map<String, CommandHandler> {
        return commands.associateBy { it.name }
    }
}