package instacli.cli

import instacli.script.commands.*
import instacli.script.commands.UserInput
import instacli.script.commands.http.*
import instacli.script.execution.CommandHandler
import instacli.script.files.ExecuteCliScriptFile

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
        ScriptInfoHandler(),
        Input(),
        Output(),
        AskUser(),
        AskAll(),
        UserInput(),
        MockAnswers(),

        // Variables
        As(),
        SetVariable(),
        ApplyVariables(),

        // Data manipulation
        CreateObject(),
        Join(),
        Merge(),
        Replace(),

        // Call other files
        ExecuteCliScriptFile(),

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
    )

    private fun commandMap(vararg commands: CommandHandler): Map<String, CommandHandler> {
        return commands.associateBy { it.name }
    }
}