package hes.yay.commands.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import hes.yay.core.*
import hes.yay.core.Yaml.parse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking

class HttpEndpoint : CommandHandler("Http endpoint"), ObjectHandler, ValueHandler {

    companion object {
        const val HTTP_DEFAULTS = "_http.defaults"
    }

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        context.variables[HTTP_DEFAULTS] = objectNode("url", data.textValue())
        return null
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        context.variables[HTTP_DEFAULTS] = data
        return null
    }
}


class HttpGet : CommandHandler("Http GET"), ValueHandler, ObjectHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        val path = objectNode("path", data.textValue())
        return processRequest(path, context, HttpMethod.Get)
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return processRequest(data, context, HttpMethod.Get)
    }
}

class HttpPost : CommandHandler("Http POST"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return processRequest(data, context, HttpMethod.Post)
    }
}

data class HttpParameters(
    val host: String,
    val path: String,
    val method: HttpMethod,
    val body: String?,
    val headers: JsonNode?,
    val cookies: JsonNode?
) {

    val url: String
        get() = "$host$path"

    companion object {
        fun create(data: ObjectNode, defaults: JsonNode?, method: HttpMethod): HttpParameters {
            update(data, defaults)

            return HttpParameters(
                host = data.get("url").textValue(),
                path = data.get("path").textValue(),
                method = method,
                body = data.get("body")?.toString(),
                headers = data.get("headers"),
                cookies = data.get("cookies")
            )
        }

        private fun update(
            data: ObjectNode,
            defaults: JsonNode?
        ) {
            if (defaults == null || defaults !is ObjectNode) {
                return
            }

            for (default in defaults.fields()) {
                // Add fields that don't exist
                data.putIfAbsent(default.key, default.value)

                // Merge dictionaries like 'headers' and 'cookies'
                if (data.has(default.key) && data.get(default.key) is ObjectNode) {
                    update(data.get(default.key) as ObjectNode, default.value)
                }
            }
        }
    }
}

private fun processRequest(data: ObjectNode, context: ScriptContext, method: HttpMethod): JsonNode? {
    return runBlocking {
        processRequest(HttpParameters.create(data, context.variables[HttpEndpoint.HTTP_DEFAULTS], method))
    }
}

private suspend fun processRequest(parameters: HttpParameters): JsonNode? {
    // TODO Authorization

    val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
        install(HttpCookies)
    }

    val response: HttpResponse =
        client.request(parameters.url) {
            method = parameters.method
            contentType(ContentType.Application.Json)
            cookies(parameters)
            headers(parameters)

            body(parameters)
        }

    val body = response.body<String>()
    return if (body.isNotEmpty()) parse(body) else null

}

private fun HttpRequestBuilder.headers(parameters: HttpParameters) {
    parameters.headers ?: return
    for (header in parameters.headers.fields()) {
        header(header.key, header.value.textValue())
    }
}

private fun HttpRequestBuilder.cookies(parameters: HttpParameters) {
    parameters.cookies ?: return
    for (cookie in parameters.cookies.fields()) {
        cookie(cookie.key, cookie.value.textValue())
    }
}

private fun HttpRequestBuilder.body(parameters: HttpParameters) {
    parameters.body ?: return
    setBody(parameters.body)
}
