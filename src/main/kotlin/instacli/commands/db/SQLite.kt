package instacli.commands.db

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.*
import instacli.util.Json.newArray
import instacli.util.Json.newObject
import instacli.util.Yaml
import instacli.util.toDomainObject
import java.lang.String.valueOf
import java.sql.Connection
import java.sql.DriverManager
import kotlin.use

object SQLite : CommandHandler("SQLite", "instacli/db"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val sql = data.toDomainObject(SQLiteData::class)

        DriverManager.getConnection("jdbc:sqlite:${sql.file}").use { connection ->
            sql.update.forEach {
                connection.doUpdate(it)
            }

            val result = sql.query?.let {
                connection.doQuery(it).toNode()
            }

            return result
        }
    }
}

fun Connection.doUpdate(update: String) {
    this.createStatement().use { statement ->
        statement.executeUpdate(update)
    }
}

fun Connection.doQuery(query: String): List<Map<String, Any>> {
    val results = mutableListOf<Map<String, Any>>()
    this.createStatement().use { statement ->
        statement.executeQuery(query).use { resultSet ->
            while (resultSet.next()) {
                val row = mutableMapOf<String, Any>()
                for (i in 1..resultSet.metaData.columnCount) {
                    row[resultSet.metaData.getColumnName(i)] = resultSet.getObject(i)
                }
                results.add(row)
            }
        }
    }
    return results
}

fun List<Map<String, Any>>.toNode(): JsonNode {
    val node = newArray()
    this.forEach { row ->
        val rowNode = newObject()
        row.forEach { (key, value) ->
            rowNode.set<JsonNode>(key, Yaml.parse(valueOf(value)))
        }
        node.add(rowNode)
    }
    return node
}