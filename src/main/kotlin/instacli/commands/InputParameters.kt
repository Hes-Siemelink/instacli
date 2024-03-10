package instacli.commands

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import instacli.cli.infoString

class InputData {

    @JsonAnySetter
    var parameters: Map<String, ParameterData> = mutableMapOf()

    constructor()
    constructor(parameters: Map<String, ParameterData>) {
        this.parameters = parameters
    }

    fun contains(option: String): Boolean {
        return parameters.contains(option)
    }

    fun toDisplayString(): String {

        val builder = StringBuilder()

        val width = parameters.maxOf { it.key.length } + 2
        parameters.forEach {
            val key = buildString {
                append("--")
                append(it.key)
                if (it.value.shortOption != null) {
                    append(", -")
                    append(it.value.shortOption)
                }
            }
            builder.append("  ")
            builder.append(infoString(key, it.value.description, width))
            builder.appendLine()
        }

        return builder.toString()
    }
}

class ParameterData {

    var description: String = ""
    var default: JsonNode? = null
    var type: String = ""
    var choices: List<JsonNode>? = null
    var display: String? = null
    var value: String? = null
    var condition: JsonNode? = null

    @JsonProperty("short option")
    var shortOption: String? = null

    constructor()
    constructor(textValue: String) {
        description = textValue
    }

    fun parseCondition(): Condition? {
        return condition?.let {
            parseCondition(it)
        }
    }
}