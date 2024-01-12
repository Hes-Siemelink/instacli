package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.script.*
import instacli.util.Yaml.parse
import instacli.util.objectNode
import instacli.util.toDisplayString
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import java.net.URI
import java.nio.file.Path
import kotlin.collections.set

class HttpEndpoint : CommandHandler("Http endpoint"), ObjectHandler, ValueHandler {

    companion object {
        private const val HTTP_DEFAULTS = "http.defaults"

        fun store(context: ScriptContext, value: JsonNode) {
            context.session[HTTP_DEFAULTS] = value
        }

        fun getFrom(context: ScriptContext): JsonNode? {
            return context.session[HTTP_DEFAULTS] as JsonNode?
        }
    }

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        store(context, objectNode("url", data.textValue()))
        return null
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        store(context, data)
        return null
    }

}


class HttpGet : CommandHandler("GET"), ValueHandler, ObjectHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return processRequestWithoutBody(data, context, HttpMethod.Get)
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return processRequest(data, context, HttpMethod.Get)
    }
}

class HttpPost : CommandHandler("POST"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return processRequest(data, context, HttpMethod.Post)
    }
}

class HttpPut : CommandHandler("PUT"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return processRequest(data, context, HttpMethod.Put)
    }
}

class HttpPatch : CommandHandler("PATCH"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return processRequest(data, context, HttpMethod.Patch)
    }
}

class HttpDelete : CommandHandler("DELETE"), ValueHandler, ObjectHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return processRequestWithoutBody(data, context, HttpMethod.Delete)
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return processRequest(data, context, HttpMethod.Delete)
    }
}

private fun encodePath(path: String?): String {
    return path?.replace(' ', '+') ?: ""
}

data class HttpParameters(
    val host: String,
    val path: String?,
    val method: HttpMethod,
    val body: JsonNode?,
    val headers: JsonNode?,
    val cookies: JsonNode?,
    val saveAs: String?,
    val username: String?,
    val password: String?,
) {

    val url: String = "$host${encodePath(path)}"

    companion object {
        fun create(data: ObjectNode, defaults: JsonNode?, method: HttpMethod): HttpParameters {
            update(data, defaults)

            return HttpParameters(
                host = data["url"].textValue(),
                path = data["path"]?.textValue(),
                method = method,
                body = data["body"],
                headers = data["headers"],
                cookies = data["cookies"],
                saveAs = data["save as"]?.textValue(),
                username = data["username"]?.textValue(),
                password = data["password"]?.textValue(),
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
                if (data.has(default.key) && data[default.key] is ObjectNode) {
                    update(data[default.key] as ObjectNode, default.value)
                }
            }
        }
    }
}

private fun processRequestWithoutBody(data: ValueNode, context: ScriptContext, method: HttpMethod): JsonNode? {

    val uri = URI(encodePath(data.textValue()))
    val separator = data.textValue().indexOf(uri.path)
    val parsedData = objectNode("path", uri.toString().substring(separator))

    val url = uri.toString().substring(0, separator)
    if (url.isNotEmpty()) {
        parsedData.put("url", url)
    }
    return processRequest(parsedData, context, method)
}

private fun processRequest(data: ObjectNode, context: ScriptContext, method: HttpMethod): JsonNode? {
    return runBlocking {
        processRequest(HttpParameters.create(data, HttpEndpoint.getFrom(context), method))
    }
}

private suspend fun processRequest(parameters: HttpParameters): JsonNode? {

    val client = HttpClient {
        if (parameters.username != null) {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(username = parameters.username, password = parameters.password ?: "")
                    }
                }
            }
        }
        // install(HttpCookies)  // Tripped up by dates in Spotify cookies.
    }

    val response: HttpResponse =
        client.request(parameters.url) {
            method = parameters.method
            cookies(parameters)

            headers(parameters)
            if (!headers.contains(HttpHeaders.ContentType)) {
                contentType(ContentType.Application.Json)
            }

            if (!headers.contains(HttpHeaders.Accept)) {
                accept(ContentType.Any)
            }
            body(parameters)
        }

    return parseResponse(response, parameters)
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
    if (headers[HttpHeaders.ContentType] == ContentType.Application.FormUrlEncoded.toString()) {
        val formData = Parameters.build {
            parameters.body.fields().forEach {
                append(it.key, it.value.toDisplayString())
            }
        }
        setBody(FormDataContent(formData))
    } else {
        setBody(parameters.body.toString())
    }
}

private suspend fun parseResponse(
    response: HttpResponse,
    parameters: HttpParameters
): JsonNode? {

    // Error
    if (!response.status.isSuccess()) {
        throw InstacliException("$response\n${response.bodyAsText()}")
    }

    // No content
    if (response.contentLength() == 0.toLong()) return null

    // Save to file
    if (parameters.saveAs != null) {
        streamBodyToFile(response, Path.of(parameters.saveAs))
        return null
    }

    // Parse body
    return try {
        // Parse result as JSON node
        val body = response.body<String>()
        parse(body)
    } catch (e: Exception) {
        // If there are any parsing or encoding errors, just return a String in TextNode
        val byteArrayBody: ByteArray = response.body()
        TextNode(String(byteArrayBody))
    }
}

suspend fun streamBodyToFile(response: HttpResponse, file: Path) {
    response.bodyAsChannel().copyTo(file.toFile().writeChannel())
}
