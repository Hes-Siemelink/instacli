package instacli.script.execution

interface CommandInfo {
    val name: String
    val summary: String

    fun infoString(width: Int = 10): String {
        return String.format("%-${width}s - ${summary.trim()}", name)
    }
}