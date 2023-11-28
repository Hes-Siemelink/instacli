package instacli.cli

import instacli.commands.*
import instacli.script.CommandHandler

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
        Prompt(),
        PromptAll(),

        // Variables
        As(),
        SetVariable(),
        ApplyVariables(),

        // Data manipulation
        Merge(),
        Add(),
        Replace(),
        Size(),
        Sort(),

        // Call other files
        RunScript(),

        // Connections
        GetAccount(),
        ConnectTo(),
        CreateAccount(),
        GetAccounts(),
        SetDefaultAccount(),
        DeleteAccount(),

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

        // Testing & Documentation
        TestCase(),
        CodeExample(),
        AssertEquals(),
        AssertThat(),
        ExpectedOutput(),
        StockAnswers(),

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