package hes.yak.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import hes.yak.*
import hes.yak.Yaml.Companion.parse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking

class HttpEndpoint: CommandHandler("Http endpoint") {

    companion object {
        val HTTP_DEFAULTS = "_http.defaults"
    }

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {

        if (data is TextNode) {
            val url = ObjectNode(JsonNodeFactory.instance)
            url.put("url", data.textValue())
            context.variables.put(HTTP_DEFAULTS, url)
        } else if (data is ObjectNode) {
            context.variables.put(HTTP_DEFAULTS, data)
        } else {
            throw ScriptException("Unsupported node type for HttpEndpoint: ${data.javaClass}", data)
        }

        return null
    }
}


class HttpGet: CommandHandler("Http GET"), ListProcessor {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        if (data is TextNode) {
            val path = ObjectNode(JsonNodeFactory.instance)
            path.put("path", data.textValue())
            return processRequest(path, context)
        } else if (data is ObjectNode) {
            return processRequest(data, context)
        } else {
            throw ScriptException("Unsupported node type for HttpEndpoint: ${data.javaClass}", data)
        }
    }
}


private fun processRequest(data: ObjectNode, context: ScriptContext): JsonNode? {
    // TODO Cookies
    // TODO Headers
    // TODO Authorization

    // Fill with defaults
    if (context.variables.containsKey(HttpEndpoint.HTTP_DEFAULTS)) {
        for (default in context.variables[HttpEndpoint.HTTP_DEFAULTS]!!.fields()) {
            data.putIfAbsent(default.key, default.value)
        }
    }
    val url = "${data["url"].textValue()}${data["path"].textValue()}"
    val client = HttpClient() {
        install(ContentNegotiation) {
            json()
        }
    }

    val response: HttpResponse = runBlocking {
        client.request(url) {
            method = HttpMethod.Get
        }
    }

    return runBlocking { parse(response.body<String>()) }
}

