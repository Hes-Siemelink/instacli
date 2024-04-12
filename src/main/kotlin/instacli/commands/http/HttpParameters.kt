package instacli.commands.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import io.ktor.http.*

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

