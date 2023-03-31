package hes.yak.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
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

class HttpEndpoint: CommandHandler("Http endpoint"), ObjectHandler, ValueHandler {

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


class HttpGet: CommandHandler("Http GET"), ValueHandler, ObjectHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        val path = objectNode("path", data.textValue())
        return processRequest(path, context, HttpMethod.Get)
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return processRequest(data, context, HttpMethod.Get)
    }
}

class HttpPost: CommandHandler("Http POST"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return processRequest(data, context, HttpMethod.Post)
    }
}

data class HttpParameters(
    val host: String,
    val path: String,
    val method: HttpMethod,
    val body: String?,
    val headers: JsonNode?) {

    val url: String
        get() = "$host$path"

    companion object {
        fun create(data: ObjectNode, defaults: JsonNode?, method: HttpMethod): HttpParameters {
            if (defaults != null) {
                for (default in defaults.fields()) {
                    data.putIfAbsent(default.key, default.value)
                }
            }

            return HttpParameters(
                host = data.get("url").textValue(),
                path = data.get("path").textValue(),
                method = method,
                body = data.get("body")?.toString(),
                headers = data.get("headers")
            )
        }
    }
}

private fun processRequest(data: ObjectNode, context: ScriptContext, method:HttpMethod): JsonNode {
    return processRequest(HttpParameters.create(data, context.variables[HttpEndpoint.HTTP_DEFAULTS], method))
}

private fun processRequest(parameters: HttpParameters): JsonNode {
    // TODO Cookies
    // TODO Authorization

    val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    val response: HttpResponse = runBlocking {
        client.request(parameters.url) {
            method = parameters.method
            if (parameters.headers != null) {
                for (header in parameters.headers.fields()) {
                    header(header.key, header.value.textValue())
                }
            }
            if (parameters.body != null) {
                setBody(parameters.body)
            }
        }
    }

    return runBlocking { parse(response.body<String>()) }
}

