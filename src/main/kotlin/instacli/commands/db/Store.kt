package instacli.commands.db

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.*
import instacli.util.Json.newArray
import instacli.util.Json.newObject
import instacli.util.Yaml
import instacli.util.toCompactJson
import instacli.util.toDomainObject
import java.sql.Connection
import java.sql.DriverManager
import kotlin.use

object Store : CommandHandler("Store", "instacli/db"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val command = data.toDomainObject(StoreData::class)

        DriverManager.getConnection("jdbc:sqlite:${command.file}").use { connection ->

            // Create table
            val createTable = "create table if not exists ${command.table} (id integer primary key, json text)"
            connection.doUpdate(createTable)

            // Insert data
            command.insert.forEach {
                val json = it.toCompactJson()
                val insert = "insert into ${command.table} (json) values ('$json')"
                connection.doUpdate(insert)
            }

            // Perform query
            return query(command, connection)
        }
    }
}

private fun query(
    command: StoreData,
    connection: Connection
): JsonNode? {
    if (command.query == null) {
        return null
    }

    val select = if (command.query.select.isEmpty()) {
        "json"
    } else {
        command.query.select.joinToString(", ") { column ->
            column.asJsonSelect()
        }
    }
    val where = command.query.where?.let {
        " where ${it.expandJsonColumns()}"
    } ?: ""

    val query = "select $select from ${command.table}$where"

    return connection.doJsonQuery(query)
}

private fun String.asJsonSelect(): String {
    return "json_extract(json, '$.$this') as $this"
}

fun String.expandJsonColumns(): String {
    // A regular expression that matches strings like '$.something'
    val jsonPath = """\$\.\S+""".toRegex()
    return this.replace(jsonPath, "json_extract(json, '$0')")
}

fun Connection.doJsonQuery(query: String): JsonNode {

    // println("Query: $query")

    val results = newArray()

    createStatement().use { statement ->
        statement.executeQuery(query).use { resultSet ->
            while (resultSet.next()) {

                if (resultSet.metaData.columnCount == 1 && resultSet.metaData.getColumnName(1) == "json") {
                    val row = Yaml.parse(resultSet.getObject(1).toString())
                    results.add(row)
                } else {
                    for (i in 1..resultSet.metaData.columnCount) {
                        val row = newObject()
                        row.set<JsonNode>(
                            resultSet.metaData.getColumnName(i),
                            Yaml.parse(resultSet.getObject(i).toString())
                        )
                        results.add(row)
                    }
                }
            }
        }
    }

    return results
}
