package instacli

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import instacli.util.Yaml
import io.javalin.Javalin

object TestServer {

    fun create(): Javalin {
        val server = Javalin.create()

        server.get("/items") { ctx ->
            ctx.json(toJson(listOf("1", "2", "3")))
        }
        server.post("/items") { ctx ->
            val request = Yaml.parse(ctx.body())
            ctx.json(request.fieldNames().asSequence())
        }
        server.put("/items") { ctx ->
            val request = Yaml.parse(ctx.body())
            ctx.json(request.fieldNames().asSequence())
        }
        server.patch("/items") { ctx ->
            val request = Yaml.parse(ctx.body())
            ctx.json(request.fieldNames().asSequence())
        }
        server.delete("/items") {
        }
        server.get("/echo/header/Test") { ctx ->
            ctx.json(ctx.headerMap())
        }
        server.get("/echo/cookies") { ctx ->
            ctx.json(mapOf("cookies" to ctx.cookieMap()))
        }
        server.get("/echo/query") { ctx ->
            val content = ctx.queryParam("content")
            ctx.json(mapOf("content" to content))
        }

        return server
    }
}

fun toJson(items: List<String>): JsonNode {
    val node = ArrayNode(JsonNodeFactory.instance)
    items.forEach { node.add(it) }
    return node
}