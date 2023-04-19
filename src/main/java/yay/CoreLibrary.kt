package yay

import yay.commands.*
import yay.commands.Set
import yay.commands.http.HttpEndpoint
import yay.commands.http.HttpGet
import yay.commands.http.HttpPost
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

        // Files
        ReadFile(),

        // Testing
        TestCase(),
        AssertEquals(),
        AssertThat(),
        ExpectedOutput(),

        // Util
        Print(),
        Task(),
        Wait(),
    )

    private fun commandMap(vararg commands: CommandHandler): Map<String, CommandHandler> {
        return commands.associateBy { it.name }
    }
}