package instacli.commands

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.script.*
import instacli.util.Yaml
import io.javalin.Javalin
import io.javalin.http.HandlerType

const val DEFAULT_PORT = 25125
private val methods = mapOf(
    "get" to HandlerType.GET,
    "post" to HandlerType.POST,
    "put" to HandlerType.PUT,
    "patch" to HandlerType.PATCH,
    "delete" to HandlerType.DELETE
)

object HttpServer {
    val server = Javalin.create()
    var context: ScriptContext? = null // FIXME dirty hack to get a context
}

object HttpServe : CommandHandler("Http serve"), ObjectHandler {
    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val serveData: HttpServeData = Yaml.parse(data)

        serveData.paths.forEach {
            HttpServer.server.addHandler(it.key, it.value)
        }

        println("Starting Http server for ${context.cliFile}")

        HttpServer.context = context
        HttpServer.server.start(DEFAULT_PORT)

        return null
    }
}

fun Javalin.addHandler(path: String, data: PathData) {
    data.endpoints.forEach {
        this.addHandler(path, it.key, it.value)
    }
}

fun Javalin.addHandler(path: String, method: String, data: EndpointData) {
    val methodType = methods[method] ?: throw CommandFormatException("Unsupported HTTP method: $method")
    this.addHandler(methodType, path) { ctx ->
        println("$methodType $path")

        // Execute script
        // TODO get a proper context
        val context = HttpServer.context ?: error("No context set on HttpServer")
        val output = data.script?.runScript(context)

        // Return result of script
        output?.let {
            ctx.json(it)
        }
    }
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
    var script: JsonNode? = null
}
