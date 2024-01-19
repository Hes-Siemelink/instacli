package instacli.cli

import instacli.commands.*
import instacli.script.CommandHandler

object CommandLibrary {
    val commands = commandMap(

        // Script definition
        ScriptInfo,

        // Variables
        As,
        ApplyVariables,
        Output,

        // Testing
        TestCase,
        CodeExample,
        AssertEquals,
        AssertThat,
        ExpectedOutput,
        StockAnswers,

        // Control flow
        Do,
        Exit,
        If,
        When,
        ForEach,
        Repeat,
        Find,

        // User interaction
        Prompt,
        PromptAll,

        // Data manipulation
        Add,
        AddTo,
        Fields,
        Replace,
        Size,
        Sort,
        Values,

        // Util
        Task,
        Print,
        Wait,
        Base64Encode,
        Base64Decode,

        // Files
        ReadFile,
        SaveAs,

        // Call other scripts
        RunScript,
        Shell,

        // HTTP
        HttpEndpoint,
        HttpGet,
        HttpPost,
        HttpPut,
        HttpPatch,
        HttpDelete,
        HttpServe,

        // Account connections
        GetAccount,
        ConnectTo,
        CreateAccount,
        GetAccounts,
        SetDefaultAccount,
        DeleteAccount,
    )

    private fun commandMap(vararg commands: CommandHandler): Map<String, CommandHandler> {
        return commands.associateBy { it.name }
    }
}