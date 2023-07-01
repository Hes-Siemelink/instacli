package instacli.cli

import instacli.commands.*
import instacli.commands.http.*
import instacli.core.CommandHandler

object CoreLibrary {
    val commands = commandMap(

        // Control flow
        Do(),
        If(),
        When(),
        ForEach(),
        Repeat(),
        ExecuteCliScriptFile(),
        Meta(),

        // Input and Output
        Input(),
        Output(),
        CheckInput(),
        UserInput(),

        // Variables
        As(),
        SetVariable(),
        ApplyVariables(),

        // Data manipulation
        CreateObject(),
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