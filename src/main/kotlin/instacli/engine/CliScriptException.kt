package instacli.engine

import com.fasterxml.jackson.databind.JsonNode
import instacli.util.Yaml

class CliScriptException(message: String, var data: JsonNode? = null, cause: Throwable? = null) :
    Exception(message, cause) {

    override val message: String?
        get() = if (data != null) {
            val yaml = Yaml.toString(data).prependIndent("  ")
            "${super.message}\n${yaml}"
        } else {
            super.message
        }
}