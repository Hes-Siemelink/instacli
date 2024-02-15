package instacli.commands

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import instacli.cli.CliFile
import instacli.script.*
import instacli.util.Yaml
import instacli.util.objectNode
import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.http.HandlerType
import io.javalin.http.bodyAsClass
import kotlin.io.path.name

const val REQUEST_VARIABLE = "request"

private val methods = mapOf(
    "get" to HandlerType.GET,
    "post" to HandlerType.POST,
    "put" to HandlerType.PUT,
    "patch" to HandlerType.PATCH,
    "delete" to HandlerType.DELETE
)

object HttpServer : CommandHandler("Http server"), ObjectHandler, DelayedVariableResolver {

    private val servers = mutableMapOf<Int, Javalin>()

    fun stop(port: Int) {
        print("Stopping Instacli Http Server on port $port\")")
        servers[port]?.stop()
        servers.remove(port)
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val port = data.getParameter("port").intValue()

        // Stop server
        data["stop"]?.let {
            if (it.booleanValue() == true) {
                stop(port)
                return null
            }
        }

        // Add endpoints
        val serveData: HttpEndpoints = Yaml.parse(data.getParameter("endpoints"))
        serveData.paths.forEach {
            addHandler(port, it.key, it.value, context)
        }

        return null
    }

    fun addHandler(port: Int, path: String, data: PathData, context: ScriptContext) {
        val server = servers.getOrPut(port) {
            println("Starting Instacli Http Server for ${context.cliFile.name} on port $port")
            Javalin.create().start(port)
        }
        data.methodHandlers.forEach {
            server.addHandler(path, it.key, it.value, context)
        }
    }
}


fun Javalin.addHandler(path: String, method: String, data: HandlerData, scriptContext: ScriptContext) {
    val methodType = methods[method] ?: throw CommandFormatException("Unsupported HTTP method: $method")
    this.addHandler(methodType, path) { httpContext ->
        handleRequest(methodType, data, httpContext, scriptContext)
    }
}

private fun handleRequest(
    method: HandlerType,
    data: HandlerData,
    httpContext: Context,
    scriptContext: ScriptContext
) {
//    println("$method ${httpContext.path()}")

    val localContext = scriptContext.clone()
    localContext.addInputVariable(httpContext)
    localContext.addRequestVariable(httpContext)

    // Execute script
    val output =
        when {
            data.output != null -> {
                resolveVariables(data.output!!, localContext.variables)
            }

            data.script != null -> {
                data.script?.runScript(localContext)
            }

            data.file != null -> {
                val script = localContext.scriptDir.resolve(data.file!!)
                CliFile(script).run(localContext)
            }

            else -> {
                throw CliScriptException("No handler action defined")
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

    val requestData = objectNode()

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
    return objectNode(headerMap())
}

fun Context.pathParametersAsJson(): ObjectNode {
    return objectNode(pathParamMap())
}

fun Context.queryParametersAsJson(): ObjectNode {
    val queryParameters = objectNode()
    for (variable in queryParamMap()) {
        queryParameters.set<JsonNode>(variable.key, TextNode(variable.value[0]))  // FIXME deal with list properly
    }
    return queryParameters
}

fun Context.bodyAsJson(): JsonNode {
    return if (body().isNotEmpty()) {
        bodyAsClass()
    } else {
        objectNode()
    }
}

fun Context.cookiesAsJson(): ObjectNode {
    return objectNode(cookieMap())
}


class HttpEndpoints {
    @JsonAnySetter
    var paths: MutableMap<String, PathData> = mutableMapOf()
}

class PathData {
    @JsonAnySetter
    var methodHandlers: Map<String, HandlerData> = mutableMapOf()
}

class HandlerData {
    var output: JsonNode? = null
    var script: JsonNode? = null
    var file: String? = null

    constructor()
    constructor(textValue: String) {
        file = textValue
    }
}
