package instacli.script.commands.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.script.execution.CommandHandler
import instacli.script.execution.ObjectHandler
import instacli.script.execution.ScriptContext
import instacli.script.execution.ValueHandler
import instacli.util.Yaml
import instacli.util.Yaml.parse
import instacli.util.objectNode
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import java.io.File
import java.net.URI

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
        val uri = URI(data.textValue())
        val separator = data.textValue().indexOf(uri.path)
        val parsedData = objectNode("path", uri.toString().substring(separator))

        val url = uri.toString().substring(0, separator)
        if (url.isNotEmpty()) {
            parsedData.put("url", url)
        }
        return processRequest(parsedData, context, HttpMethod.Get)
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

class HttpPut : CommandHandler("Http PUT"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return processRequest(data, context, HttpMethod.Put)
    }
}

class HttpPatch : CommandHandler("Http PATCH"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return processRequest(data, context, HttpMethod.Patch)
    }
}

class HttpDelete : CommandHandler("Http DELETE"), ValueHandler, ObjectHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        val path = objectNode("path", data.textValue())
        return processRequest(path, context, HttpMethod.Delete)
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return processRequest(data, context, HttpMethod.Delete)
    }
}


data class HttpParameters(
    val host: String,
    val path: String?,
    val method: HttpMethod,
    val body: JsonNode?,
    val headers: JsonNode?,
    val cookies: JsonNode?,
    val saveAs: String?
) {

    val url: String
        get() = "$host${(path ?: "")}"

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
                saveAs = data["save as"]?.textValue()
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

private fun processRequest(data: ObjectNode, context: ScriptContext, method: HttpMethod): JsonNode? {
    return runBlocking {
        processRequest(HttpParameters.create(data, context.variables[HttpEndpoint.HTTP_DEFAULTS], method))
    }
}

private suspend fun processRequest(parameters: HttpParameters): JsonNode? {
    // TODO Authorization

    val client = HttpClient {
        install(HttpCookies)
    }

    val response: HttpResponse =
        client.request(parameters.url) {
            method = parameters.method
            cookies(parameters)

            headers(parameters)
            if (!headers.contains(HttpHeaders.ContentType)) {
                contentType(ContentType.Application.Json)
            }

            accept(ContentType.Any)
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
                append(it.key, Yaml.toString(it.value))
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

    // No content
    if (response.contentLength() == 0.toLong()) return null

    // Save to file
    if (parameters.saveAs != null) {
        streamBodyToFile(response, File(parameters.saveAs))
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

suspend fun streamBodyToFile(response: HttpResponse, file: File) {
    response.bodyAsChannel().copyTo(file.writeChannel())
}
