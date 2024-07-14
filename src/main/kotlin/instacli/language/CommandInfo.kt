package instacli.language

interface CommandInfo {
    val name: String
    val description: String
    val hidden: Boolean
    val instacliSpec: String
}