package instacli.cli

import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.ListViewOptions
import com.github.kinquirer.components.promptListObject
import com.github.kinquirer.core.Choice
import instacli.script.execution.CliScript
import instacli.script.execution.CommandInfo
import instacli.script.execution.InputInfo

interface UserInput {
    fun askForCommand(commands: List<CommandInfo>): String
}

interface UserOutput {
    fun printCommands(commands: List<CommandInfo>)
    fun printScriptInfo(script: CliScript)

}

object ConsoleInput : UserInput {
    override fun askForCommand(commands: List<CommandInfo>): String {
        val width = commands.maxOf { it.name.length }
        val selectedCommand = KInquirer.promptListObject(
            message = "Available commands:",
            choices = commands.map { Choice(infoString(it.name, it.description, width), it.name) },
            viewOptions = ListViewOptions(
                questionMarkPrefix = "*",
                cursor = " > ",
                nonCursor = "   ",
            )
        )
        println("---")
        return selectedCommand
    }
}

object ConsoleOutput : UserOutput {
    override fun printCommands(commands: List<CommandInfo>) {
        println("Available commands:")

        val width = commands.maxOf { it.name.length }
        commands.forEach {
            println("  ${infoString(it.name, it.description, width)}")
        }
    }

    override fun printScriptInfo(script: CliScript) {

        if (script.description != null) {
            println(script.description)
        }

        printInputParameters(script)
    }

    private fun printInputParameters(script: CliScript) {

        val inputInfo = InputInfo.from(script.input?.data ?: return)

        println("\nInput parameters:")

        val width = inputInfo.parameters.maxOf { it.key.length } + 2
        inputInfo.parameters.forEach {
            println("  ${infoString("--" + it.key, it.value.description, width)}")
        }
    }

}


fun infoString(key: String, value: String, width: Int = 10): String {
    return String.format("%-${width}s   ${value.trim()}", key)
}
