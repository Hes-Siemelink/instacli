package instacli.cli

import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.ListViewOptions
import com.github.kinquirer.components.promptListObject
import com.github.kinquirer.core.Choice
import instacli.commands.InputInfo
import instacli.engine.CliScript
import instacli.engine.CommandInfo

interface UserInput {
    fun askForCommand(commands: List<CommandInfo>): String
}

interface UserOutput {
    fun printUsage()
    fun printScriptInfo(script: CliScript)
    fun printCommands(commands: List<CommandInfo>)
    fun printDirectoryInfo(info: DirectoryInfo)
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

    override fun printDirectoryInfo(info: DirectoryInfo) {
        if (info.description.isNotEmpty()) {
            println(info.description.trim())
        } else {
            println("${info.name} has several subcommands.")
        }
        println()
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

    override fun printUsage() {
        println("Instacli -- Instantly create CLI applications with light scripting!")
        println()
        println("Usage:\n   cli [-i] [--help] file | directory")
        println()
    }
}


fun infoString(key: String, value: String, width: Int = 10): String {
    return String.format("%-${width}s   ${value.trim()}", key)
}
