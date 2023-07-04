package instacli.script.execution

interface CommandInfo {
    val name: String
    val description: String

    fun infoString(width: Int = 10): String {
        return String.format("%-${width}s - ${description.trim()}", name)
    }
}