package hes.yay

import hes.yay.commands.*
import hes.yay.commands.Set
import hes.yay.commands.http.HttpEndpoint
import hes.yay.commands.http.HttpGet
import hes.yay.commands.http.HttpPost
import hes.yay.core.CommandHandler

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