package instacli.commands

import instacli.commands.connections.*
import instacli.commands.controlflow.*
import instacli.commands.datamanipulation.*
import instacli.commands.db.SQLite
import instacli.commands.db.Store
import instacli.commands.errors.ErrorCommand
import instacli.commands.errors.OnError
import instacli.commands.errors.OnErrorType
import instacli.commands.files.ReadFile
import instacli.commands.files.RunScript
import instacli.commands.files.TempFile
import instacli.commands.files.WriteFile
import instacli.commands.http.*
import instacli.commands.schema.ValidateSchema
import instacli.commands.schema.ValidateType
import instacli.commands.scriptinfo.ScriptInfo
import instacli.commands.shell.Cli
import instacli.commands.shell.Shell
import instacli.commands.testing.*
import instacli.commands.userinteraction.Confirm
import instacli.commands.userinteraction.Prompt
import instacli.commands.userinteraction.PromptObject
import instacli.commands.util.*
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
        ExpectedConsoleOutput,
        ExpectedOutput,
        ExpectedError,
        Answers,

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
        Confirm,
        Prompt,
        PromptObject,

        // Data manipulation
        Add,
        AddTo,
        Append,
        Fields,
        JsonPatch,
        Replace,
        Size,
        Sort,
        Values,

        // Util
        Print,
        PrintJson,
        ToJson,
        Wait,
        Base64Encode,
        Base64Decode,

        // Files
        ReadFile,
        WriteFile,
        TempFile,

        // Call other scripts
        Cli,
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
        ConnectTo,
        CreateCredentials,
        Credentials,
        DeleteCredentials,
        GetAllCredentials,
        GetCredentials,
        SetDefaultCredentials,

        // JSON Schema
        ValidateSchema,

        // Types
        ValidateType,

        // Database
        SQLite,
        Store
    )

    // TODO Store commands in canonical form: all lower case and spaces
    private fun commandMap(vararg commands: CommandHandler): Map<String, CommandHandler> {
        return commands.associateBy { it.name }
    }
}