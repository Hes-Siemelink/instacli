package instacli.commands.db

import com.fasterxml.jackson.databind.JsonNode

data class StoreData(
    val file: String = "",
    val table: String = "json_data",
    val insert: List<JsonNode> = emptyList<JsonNode>(),
    val query: QueryData? = null
)

data class QueryData(
    val select: List<String> = emptyList<String>(),
    val where: String? = null
)
