package instacli.commands

import instacli.commands.connections.*
import instacli.commands.controlflow.*
import instacli.commands.datamanipulation.*
import instacli.commands.errors.ErrorCommand
import instacli.commands.errors.OnError
import instacli.commands.errors.OnErrorType
import instacli.commands.files.ReadFile
import instacli.commands.files.RunScript
import instacli.commands.files.SaveAs
import instacli.commands.http.*
import instacli.commands.schema.Validate
import instacli.commands.scriptinfo.ScriptInfo
import instacli.commands.shell.Shell
import instacli.commands.testing.*
import instacli.commands.userinteraction.Prompt
import instacli.commands.userinteraction.PromptObject
import instacli.commands.util.Base64Decode
import instacli.commands.util.Base64Encode
import instacli.commands.util.Print
import instacli.commands.util.Wait
import instacli.commands.variables.As
import instacli.commands.variables.Output
import instacli.language.CommandHandler

object CommandLibrary {
    val commands = commandMap(

        // Script definition
        ScriptInfo,

        // Variables
        As,
        Output,

        // Testing
        TestCase,
        CodeExample,
        AssertEquals,
        AssertThat,
        ExpectedOutput,
        ErrorExpected,
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
        Get,
        Post,
        Put,
        Patch,
        Delete,
        HttpRequestDefaults,

        // Http server
        HttpServer,

        // Account connections
        GetCredentials,
        ConnectTo,
        CreateCredentials,
        GetAllCredentials,
        SetDefaultCredentials,
        DeleteCredentials,

        // JSON Schema
        Validate,
    )

    private fun commandMap(vararg commands: CommandHandler): Map<String, CommandHandler> {
        return commands.associateBy { it.name }
    }
}