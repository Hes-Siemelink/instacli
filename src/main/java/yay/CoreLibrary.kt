package yay

import yay.commands.*
import yay.commands.Set
import yay.commands.http.*
import yay.core.CommandHandler

object CoreLibrary {
    val commands = commandMap(

        // Control flow
        Do(),
        If(),
        IfAny(),
        ForEach(),
        Repeat(),
        ExecuteYayFile(),

        // Input and Output
        Input(),
        Output(),
        CheckInput(),
        UserInput(),

        // Variables
        As(),
        SetVariable(),
        Set(),
        ApplyVariables(),

        // Data manipulation
        Join(),
        Merge(),
        Replace(),

        // HTTP
        HttpEndpoint(),
        HttpGet(),
        HttpPost(),
        HttpPut(),
        HttpPatch(),
        HttpDelete(),

        // Files
        ReadFile(),

        // Testing
        TestCase(),
        AssertEquals(),
        AssertThat(),
        ExpectedOutput(),

        // Util
        Task(),
        Print(),
        PrintAsYaml(),
        Wait(),
    )

    private fun commandMap(vararg commands: CommandHandler): Map<String, CommandHandler> {
        return commands.associateBy { it.name }
    }
}