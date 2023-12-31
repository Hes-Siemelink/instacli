package instacli.spec

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.TextNode
import instacli.util.Yaml
import io.javalin.Javalin

val greetings  = mapOf(
    "English" to "Hi",
    "Spanish" to "¡Hola",
    "Dutch" to "Hoi")

object InstacliSampleServer {

    fun create(): Javalin {
        val server = Javalin.create()

        server.post("/greeting") { ctx ->
            val body = Yaml.parse(ctx.body())
            val name = body["name"].textValue()
            val greeting = greetings[body["language"].textValue()]
            ctx.json(TextNode("$greeting $name!"))
        }
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

fun main() {
    println("Starting Instacli Sample Server")
    InstacliSampleServer.create().start(25125)
}