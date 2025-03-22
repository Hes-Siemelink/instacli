package instacli.commands.http

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import instacli.files.CliFile
import instacli.language.*
import instacli.util.Json
import instacli.util.Yaml
import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.http.HandlerType
import io.javalin.http.bodyAsClass
import kotlin.io.path.name

object HttpServer : CommandHandler("Http server", "instacli/http"), ObjectHandler, DelayedResolver {

    private val servers = mutableMapOf<Int, Javalin>()

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val port = data.getParameter("port").intValue()

        // Stop server
        data["stop"]?.let {
            if (it.booleanValue()) {
                stop(port)
                return null
            }
        }

        // Add endpoints
        val serveData: Endpoints = Yaml.parse(data.getParameter("endpoints"))
        serveData.paths.forEach {
            addHandler(port, it.key, it.value, context)
        }

        return null
    }

    fun stop(port: Int) {
        print("Stopping Instacli Http Server on port $port\")")
        servers[port]?.stop()
        servers.remove(port)
    }

    private fun addHandler(port: Int, path: String, data: EndpointData, context: ScriptContext) {
        val server = servers.getOrPut(port) {
            println("Starting Instacli Http Server for ${context.cliFile.name} on port $port")
            Javalin.create().start(port)
        }

        data.methodHandlers.forEach {
            server.addHandler(path, it.key, it.value, context)
        }
    }
}

const val REQUEST_VARIABLE = "request"

private val methods = mapOf(
    "get" to HandlerType.GET,
    "post" to HandlerType.POST,
    "put" to HandlerType.PUT,
    "patch" to HandlerType.PATCH,
    "delete" to HandlerType.DELETE
)

fun Javalin.addHandler(path: String, method: String, data: MethodHandlerData, scriptContext: ScriptContext) {
    val methodType = methods[method] ?: throw CommandFormatException("Unsupported HTTP method: $method")
    this.addHttpHandler(methodType, path) { httpContext ->
//        println("$method ${httpContext.path()}")
        handleRequest(data, httpContext, scriptContext)
    }
}

private fun handleRequest(
    data: MethodHandlerData,
    httpContext: Context,
    scriptContext: ScriptContext
) {

    val localContext = scriptContext.clone()
    localContext.addInputVariable(httpContext)
    localContext.addRequestVariable(httpContext)

    // Execute script
    val output =
        when {
            data.output != null -> {
                data.output.resolve(localContext)
            }

            data.script != null -> {
                data.script.run(localContext)
            }

            data.file != null -> {
                val file = localContext.scriptDir.resolve(data.file)
                CliFile(file).run(localContext)
            }

            else -> {
                throw CliScriptingException("No handler action defined")
            }
        }

    // Return result of script
    output?.let {
        httpContext.json(it)
    }
}

private fun ScriptContext.addInputVariable(httpContext: Context) {

    // Use body to populate input variable
    if (httpContext.body().isNotEmpty()) {
        variables[INPUT_VARIABLE] = httpContext.bodyAsJson()
        return
    }

    // If there is no body, use query parameters.
    if (httpContext.queryParamMap().isNotEmpty()) {
        variables[INPUT_VARIABLE] = httpContext.queryParametersAsJson()
    }
}

private fun ScriptContext.addRequestVariable(httpContext: Context) {

    val requestData = Json.newObject()

    requestData.set<JsonNode>("headers", httpContext.headersAsJson())
    requestData.set<JsonNode>("path", TextNode(httpContext.path()))
    requestData.set<JsonNode>("pathParameters", httpContext.pathParametersAsJson())
    requestData.set<JsonNode>("query", TextNode(httpContext.queryString() ?: ""))
    requestData.set<JsonNode>("queryParameters", httpContext.queryParametersAsJson())
    requestData.set<JsonNode>("body", httpContext.bodyAsJson())
    requestData.set<JsonNode>("cookies", httpContext.cookiesAsJson())

    variables[REQUEST_VARIABLE] = requestData
}

fun Context.headersAsJson(): ObjectNode {
    return Json.newObject(headerMap())
}

fun Context.pathParametersAsJson(): ObjectNode {
    return Json.newObject(pathParamMap())
}

fun Context.queryParametersAsJson(): ObjectNode {
    val queryParameters = Json.newObject()
    for (variable in queryParamMap()) {
        queryParameters.set<JsonNode>(variable.key, TextNode(variable.value[0]))  // FIXME deal with list properly
    }
    return queryParameters
}

fun Context.bodyAsJson(): JsonNode {
    return if (body().isNotEmpty()) {
        bodyAsClass()
    } else {
        Json.newObject()
    }
}

fun Context.cookiesAsJson(): ObjectNode {
    return Json.newObject(cookieMap())
}


data class Endpoints(
    @get:JsonAnyGetter
    val paths: MutableMap<String, EndpointData> = mutableMapOf()
)


data class EndpointData(
    @get:JsonAnyGetter
    val methodHandlers: Map<String, MethodHandlerData> = mutableMapOf()
)

data class MethodHandlerData(
    val output: JsonNode? = null,
    val script: JsonNode? = null,
    val file: String? = null
) {
    @JsonCreator
    constructor(textValue: String) : this(file = textValue)
}
