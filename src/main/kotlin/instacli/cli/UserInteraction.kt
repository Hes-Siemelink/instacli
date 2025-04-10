package instacli.cli

import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.ListViewOptions
import com.github.kinquirer.components.promptListObject
import com.github.kinquirer.core.Choice
import instacli.files.DirectoryInfo
import instacli.language.CommandInfo
import instacli.language.Script
import instacli.language.types.ObjectDefinition
import instacli.language.types.toDisplayString

interface UserInput {
    fun askForCommand(commands: List<CommandInfo>): String
}

interface ConsoleOutput {
    fun printUsage(globalOptions: CommandLineParameters)
    fun printScriptInfo(script: Script)
    fun printCommands(commands: List<CommandInfo>)
    fun printDirectoryInfo(info: DirectoryInfo)
    fun printOutput(output: String)
}

object StandardInput : UserInput {
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
//        println("---")
        return selectedCommand
    }
}

object StandardOutput : ConsoleOutput {
    override fun printCommands(commands: List<CommandInfo>) {

        if (commands.isEmpty()) {
            println("No commands available.")
        } else {
            println("Available commands:")

            val width = commands.maxOf { it.name.length }
            commands.forEach {
                println("  ${infoString(it.name, it.description, width)}")
            }
        }
    }

    override fun printDirectoryInfo(info: DirectoryInfo) {
        if (info.description.isNotEmpty()) {
            println(info.description.trim())
            println()
        }
//        else {
//            println("${info.name} has several subcommands.")
//        }
    }

    override fun printScriptInfo(script: Script) {

        if (script.info?.description != null) {
            println(script.info?.description)
        }

        printInputParameters(script)
    }

    private fun printInputParameters(script: Script) {

        val inputData: ObjectDefinition = script.info ?: return

        if (inputData.properties.isEmpty()) return

        println("\nOptions:")
        println(inputData.toDisplayString())
    }

    override fun printUsage(globalOptions: CommandLineParameters) {
        println("Instacli -- Instantly create CLI applications with light scripting!")
        println()
        println("Usage:\n   cli [global options] file | directory [command options]")
        println("\nGlobal options:")
        println(globalOptions.toDisplayString())
    }

    override fun printOutput(output: String) {
        println(output)
    }
}

fun infoString(key: String, value: String, width: Int = 10): String {
    return String.format("%-${width}s   ${value.trim()}", key)
}
