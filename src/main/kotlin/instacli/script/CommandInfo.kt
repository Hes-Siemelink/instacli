package instacli.script

interface CommandInfo {
    val name: String
    val description: String
    val hidden: Boolean
}