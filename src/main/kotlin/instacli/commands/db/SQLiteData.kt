package instacli.commands.db

data class SQLiteData(
    val file: String = "",
    val update: List<String> = emptyList<String>(),
    val query: String? = null
)
