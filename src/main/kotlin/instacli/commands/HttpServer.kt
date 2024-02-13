package instacli.commands

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.cli.CliFile
import instacli.script.*
import instacli.util.Yaml
import instacli.util.objectNode
import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.http.HandlerType
import io.javalin.http.bodyAsClass
import kotlin.io.path.name

const val DEFAULT_PORT = 2525
const val REQUEST_VARIABLE = "request"

private val methods = mapOf(
    "get" to HandlerType.GET,
    "post" to HandlerType.POST,
    "put" to HandlerType.PUT,
    "patch" to HandlerType.PATCH,
    "delete" to HandlerType.DELETE
)

object InternalHttpServer {
    var port = DEFAULT_PORT
    private val server: Javalin = Javalin.create()  // TODO Use Javalin.create().port(port)
    private var started = false

    fun addHandler(path: String, data: PathData, context: ScriptContext) {
        data.endpoints.forEach {
            server.addHandler(path, it.key, it.value, context)
        }
        start("Starting Instacli Http Server for ${context.cliFile.name} on port $port")
    }

    fun start(message: String = "Starting Instacli Http Server") {
        if (started) return

        println(message)

        server.start(port)
        started = true
    }

    fun stop() {
        server.stop()
        started = false
    }
}

object HttpServer : CommandHandler("Http server"), ValueHandler, ObjectHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        val action = data.textValue()
        when (action) {
            "start" -> {
                InternalHttpServer.start()
            }

            "stop" -> {
                InternalHttpServer.stop()
            }
        }
        return null
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val port = data["port"]?.intValue() ?: 0
        if (port != 0) {
            InternalHttpServer.port = port
        }

        return null
    }

}

object HttpEndpoints : CommandHandler("Http endpoints"), ObjectHandler, DelayedVariableResolver {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val serveData: HttpServeData = Yaml.parse(data)

        serveData.paths.forEach {
            InternalHttpServer.addHandler(it.key, it.value, context)
        }

        return null
    }
}


fun Javalin.addHandler(path: String, method: String, data: EndpointData, scriptContext: ScriptContext) {
    val methodType = methods[method] ?: throw CommandFormatException("Unsupported HTTP method: $method")
    this.addHandler(methodType, path) { httpContext ->
        handleRequest(methodType, data, httpContext, scriptContext)
    }
}

private fun handleRequest(
    method: HandlerType,
    data: EndpointData,
    httpContext: Context,
    scriptContext: ScriptContext
) {
//    println("$method ${httpContext.path()}")

    val localContext = scriptContext.clone()
    localContext.addInputVariable(httpContext)
    localContext.addRequestVariable(httpContext)

    // Execute script
    val output =
        if (data.script != null) {
            data.script?.runScript(localContext)
        } else if (data.scriptName != null) {
            val script = localContext.scriptDir.resolve(data.scriptName!!)

            CliFile(script).run(localContext)
        } else {
            throw CliScriptException("No script defined")
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

fun Context.bodyAsJson(): ObjectNode {
    return if (body().isNotEmpty()) {
        bodyAsClass()
    } else {
        objectNode()
    }
}

fun Context.cookiesAsJson(): ObjectNode {
    return objectNode(cookieMap())
}


class HttpServeData {
    @JsonAnySetter
    var paths: MutableMap<String, PathData> = mutableMapOf()
}

class PathData {
    @JsonAnySetter
    var endpoints: Map<String, EndpointData> = mutableMapOf()
}

class EndpointData {
    var scriptName: String? = null
    var script: JsonNode? = null

    constructor()
    constructor(textValue: String) {
        scriptName = textValue
    }

}
