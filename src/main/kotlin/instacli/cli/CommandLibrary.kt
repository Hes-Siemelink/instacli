package instacli.cli

import instacli.commands.*
import instacli.script.CommandHandler

object CommandLibrary {
    val commands = commandMap(

        // Script definition
        ScriptInfo(),
        Input(),
        Output(),

        // Variables
        As(),
        ApplyVariables(),

        // Testing
        TestCase(),
        CodeExample(),
        AssertEquals(),
        AssertThat(),
        ExpectedOutput(),
        StockAnswers(),

        // Control flow
        Do(),
        Exit(),
        If(),
        When(),
        ForEach(),
        Repeat(),

        // User interaction
        Prompt(),
        PromptAll(),

        // Data manipulation
        Add(),
        AddToOutput(),
        AddToVariable(),
        Replace(),
        Size(),
        Sort(),

        // Util
        Task(),
        Print(),
        PrintAsYaml(),
        Wait(),
        Base64Encode(),
        Base64Decode(),

        // Files
        ReadFile(),

        // Call other scripts
        RunScript(),
        Shell(),

        // HTTP
        HttpEndpoint(),
        HttpGet(),
        HttpPost(),
        HttpPut(),
        HttpPatch(),
        HttpDelete(),

        // Account connections
        GetAccount(),
        ConnectTo(),
        CreateAccount(),
        GetAccounts(),
        SetDefaultAccount(),
        DeleteAccount(),
    )

    private fun commandMap(vararg commands: CommandHandler): Map<String, CommandHandler> {
        return commands.associateBy { it.name }
    }
}