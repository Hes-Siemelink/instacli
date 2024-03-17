package instacli.commands

import instacli.cli.RunScript
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

        // Error handling
        ErrorCommand,
        OnError,
        OnErrorType,

        // User interaction
        Prompt,
        PromptObject,

        // Data manipulation
        Add,
        AddTo,
        Append,
        Fields,
        Replace,
        Size,
        Sort,
        Values,

        // Util
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

        // HTTP client
        HttpRequestDefaults,
        HttpGet,
        HttpPost,
        HttpPut,
        HttpPatch,
        HttpDelete,

        // Http server
        HttpServer,

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