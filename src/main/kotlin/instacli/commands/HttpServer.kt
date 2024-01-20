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

const val DEFAULT_PORT = 25125

private val methods = mapOf(
    "get" to HandlerType.GET,
    "post" to HandlerType.POST,
    "put" to HandlerType.PUT,
    "patch" to HandlerType.PATCH,
    "delete" to HandlerType.DELETE
)

object HttpServer {
    private val server: Javalin = Javalin.create()
    private var started = false

    fun addHandler(path: String, data: PathData, context: ScriptContext) {
        data.endpoints.forEach {
            server.addHandler(path, it.key, it.value, context)
        }
        startServer(context)
    }

    private fun startServer(context: ScriptContext) {
        if (started) return

        println("Starting Http server for ${context.cliFile}")

        server.start(DEFAULT_PORT)
        started = true
    }
}

object HttpEndpoint : CommandHandler("Http endpoint"), ObjectHandler, DelayedVariableResolver {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val serveData: HttpServeData = Yaml.parse(data)

        serveData.paths.forEach {
            HttpServer.addHandler(it.key, it.value, context)
        }

        return null
    }
}


fun Javalin.addHandler(path: String, method: String, data: EndpointData, mainContext: ScriptContext) {
    val methodType = methods[method] ?: throw CommandFormatException("Unsupported HTTP method: $method")
    this.addHandler(methodType, path) { httpContext ->
        println("$methodType $path")

        val localContext = mainContext.clone()
        localContext.addInputVariables(httpContext)

        // Execute script
        val output =
            when {
                data.script != null -> {
                    data.script?.runScript(localContext)
                }

                else -> {
                    val script = localContext.scriptDir.resolve(data.scriptName)

                    CliFile(script).run(localContext)
                }
            }

        // Return result of script
        output?.let {
            httpContext.json(it)
        }
    }
}

private fun ScriptContext.addInputVariables(httpContext: Context) {

    if (httpContext.body().isNotEmpty()) {
        variables[INPUT_VARIABLE] = httpContext.bodyAsClass()
        return
    }

    if (httpContext.queryParamMap().isNotEmpty()) {
        addInputVariables(httpContext.queryParamMap())
    }
}

fun ScriptContext.addInputVariables(vars: Map<String, List<String>>) {
    val input = objectNode()
    for (variable in vars) {
        input.set<JsonNode>(variable.key, TextNode(variable.value[0]))  // FIXME deal with list properly
    }
    variables[INPUT_VARIABLE] = input
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
