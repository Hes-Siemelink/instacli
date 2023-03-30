package hes.yak.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import hes.yak.Yaml
import io.javalin.Javalin

class TestServer {

    companion object {
        val app = Javalin.create()

        init {
            app.get("/items") { ctx ->
                ctx.json(toJson(listOf("1", "2", "3")))
            }
            app.post("/items") {ctx ->
                val request = Yaml.parse(ctx.body())
                ctx.json(request.fieldNames().asSequence())
            }
            app.get("/echo/header/Test") {ctx ->
                ctx.json(ctx.headerMap())
            }
        }

        fun start() {
            app.start(25125)
        }

        fun stop() {
            app.stop()
        }
    }
}

fun toJson(items: List<String>): JsonNode {
    val node = ArrayNode(JsonNodeFactory.instance)
    items.forEach { node.add(it) }
    return node
}

fun main() {
    TestServer.start()
}