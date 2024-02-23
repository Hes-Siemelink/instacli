package instacli.cli

import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.ListViewOptions
import com.github.kinquirer.components.promptListObject
import com.github.kinquirer.core.Choice
import instacli.script.CommandInfo
import instacli.script.Script

interface UserInput {
    fun askForCommand(commands: List<CommandInfo>): String
}

interface UserOutput {
    fun printUsage()
    fun printScriptInfo(script: Script)
    fun printCommands(commands: List<CommandInfo>)
    fun printDirectoryInfo(info: DirectoryInfo)
    fun println(message: String)
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
        kotlin.io.println("Available commands:")

        val width = commands.maxOf { it.name.length }
        commands
            .filter { !it.hidden }
            .forEach {
                kotlin.io.println("  ${infoString(it.name, it.description, width)}")
            }
    }

    override fun printDirectoryInfo(info: DirectoryInfo) {
        if (info.description.isNotEmpty()) {
            kotlin.io.println(info.description.trim())
        } else {
            kotlin.io.println("${info.name} has several subcommands.")
        }
        println()
    }

    override fun printScriptInfo(script: Script) {

        if (script.info?.description != null) {
            println(script.info?.description)
        }

        printInputParameters(script)
    }

    private fun printInputParameters(script: Script) {

        val inputData = script.info?.input ?: return

        kotlin.io.println("\nOptions:")

        val width = inputData.parameters.maxOf { it.key.length } + 2
        inputData.parameters.forEach {
            val key = buildString {
                append("--")
                append(it.key)
                if (it.value.shortOption != null) {
                    append(", -")
                    append(it.value.shortOption)
                }
            }
            kotlin.io.println("  ${infoString(key, it.value.description, width)}")
        }
    }

    override fun printUsage() {
        kotlin.io.println("Instacli -- Instantly create CLI applications with light scripting!")
        println()
        kotlin.io.println("Usage:\n   cli [options] file | directory")
        println()
        kotlin.io.println("Options:")
    }

    override fun println(message: String) {
        kotlin.io.println(message)
    }
}


fun infoString(key: String, value: String, width: Int = 10): String {
    return String.format("%-${width}s   ${value.trim()}", key)
}
