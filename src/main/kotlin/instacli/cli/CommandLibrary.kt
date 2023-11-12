package instacli.cli

import instacli.commands.*
import instacli.engine.CommandHandler

object CommandLibrary {
    val commands = commandMap(

        // Control flow
        Do(),
        Exit(),
        If(),
        When(),
        ForEach(),
        Repeat(),

        // Input and Output
        ScriptInfo(),
        Input(),
        Output(),
        AskUser(),
        AskAll(),
        MockAnswers(),

        // Variables
        As(),
        SetVariable(),
        ApplyVariables(),

        // Data manipulation
        CreateObject(),
        Merge(),
        Replace(),
        Size(),

        // Call other files
        ExecuteCliScriptFile(),

        // Connections
        Connection(),
        AddConnection(),

        // HTTP
        HttpEndpoint(),
        HttpGet(),
        HttpPost(),
        HttpPut(),
        HttpPatch(),
        HttpDelete(),

        // Files
        ReadFile(),

        // Shell execution
        Shell(),

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
        Base64Encode(),
        Base64Decode()
    )

    private fun commandMap(vararg commands: CommandHandler): Map<String, CommandHandler> {
        return commands.associateBy { it.name }
    }
}