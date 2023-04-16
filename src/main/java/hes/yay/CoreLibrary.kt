package hes.yay

import hes.yay.commands.*
import hes.yay.commands.Set
import hes.yay.commands.http.HttpEndpoint
import hes.yay.commands.http.HttpGet
import hes.yay.commands.http.HttpPost
import hes.yay.core.CommandHandler

object CoreLibrary {
    val commands = commandMap(
        TestCase(),
        AssertEquals(),
        AssertThat(),
        ExpectedOutput(),
        Input(),
        Output(),
        ExecuteYayFile(),
        Do(),
        ForEach(),
        Join(),
        As(),
        Merge(),
        Print(),
        ReadFile(),
        ApplyVariables(),
        Repeat(),
        If(),
        IfAny(),
        HttpEndpoint(),
        HttpGet(),
        HttpPost(),
        Replace(),
        SetVariable(),
        Set(),
        Task(),
        Wait()
    )

    private fun commandMap(vararg commands: CommandHandler): Map<String, CommandHandler> {
        return commands.associateBy { it.name }
    }
}