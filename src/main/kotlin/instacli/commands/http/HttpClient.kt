package instacli.commands.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.InstacliCommandError
import instacli.language.ScriptContext
import instacli.util.Json
import instacli.util.Yaml
import instacli.util.toDisplayYaml
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

object HttpClient {

    fun processRequest(data: ValueNode, context: ScriptContext, method: HttpMethod): JsonNode? {

        val uri = URI(encodePath(data.textValue()))
        val separator = data.textValue().indexOf(uri.path)
        val parsedData = Json.newObject("path", uri.toString().substring(separator))

        val url = uri.toString().substring(0, separator)
        if (url.isNotEmpty()) {
            parsedData.put("url", url)
        }

        return processRequest(parsedData, context, method)
    }

    fun processRequest(data: ObjectNode, context: ScriptContext, method: HttpMethod): JsonNode? {
        return runBlocking {
            processRequest(HttpParameters.create(data, HttpRequestDefaults.getFrom(context), method))
        }
    }

    private suspend fun processRequest(parameters: HttpParameters): JsonNode? {

        val client = createClient(parameters)

        val response: HttpResponse =
            client.request(parameters.url, createRequest(parameters))

        return handleResponse(response, parameters)
    }

    private fun createClient(parameters: HttpParameters) = HttpClient {
        if (parameters.username != null) {
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = parameters.username,
                            password = parameters.password ?: ""
                        )
                    }
                }
            }
        }
    }

    private fun createRequest(parameters: HttpParameters): HttpRequestBuilder.() -> Unit =
        {
            method = parameters.method

            headers(parameters)
            cookies(parameters)
            body(parameters)
        }

    private fun HttpRequestBuilder.headers(parameters: HttpParameters) {
        parameters.headers?.fields()?.forEach { header ->
            header(header.key, header.value.textValue())
        }

        if (!headers.contains(HttpHeaders.ContentType)) {
            contentType(ContentType.Application.Json)
        }
        if (!headers.contains(HttpHeaders.Accept)) {
            accept(ContentType.Any)
        }
    }

    private fun HttpRequestBuilder.cookies(parameters: HttpParameters) {
        parameters.cookies?.fields()?.forEach { cookie ->
            cookie(cookie.key, cookie.value.textValue())
        }
    }

    private fun HttpRequestBuilder.body(parameters: HttpParameters) {
        parameters.body ?: return

        if (headers[HttpHeaders.ContentType] == ContentType.Application.FormUrlEncoded.toString()) {
            val formData = Parameters.build {
                parameters.body.fields().forEach {
                    append(it.key, it.value.toDisplayYaml())
                }
            }
            setBody(FormDataContent(formData))
        } else {
            setBody(parameters.body.toString())
        }
    }

    private suspend fun handleResponse(
        response: HttpResponse,
        parameters: HttpParameters
    ): JsonNode? {

        // Error
        if (!response.status.isSuccess()) {
            val data = try {
                Yaml.parse(response.bodyAsText())
            } catch (_: Exception) {
                TextNode(response.bodyAsText())
            }
            val type = response.status.value.toString()
            throw InstacliCommandError(type, "Http request returned an error", data)
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
            Yaml.parse(body)
        } catch (e: Exception) {
            // If there are any parsing or encoding errors, just return a String in TextNode
            val byteArrayBody: ByteArray = response.body()
            TextNode(String(byteArrayBody))
        }
    }

    private suspend fun streamBodyToFile(response: HttpResponse, file: Path) {
        response.bodyAsChannel().copyTo(file.toFile().writeChannel())
    }
}

fun encodePath(path: String?): String {
    return path?.replace(' ', '+') ?: ""
}
