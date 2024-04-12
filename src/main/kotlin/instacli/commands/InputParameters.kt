package instacli.commands

import com.fasterxml.jackson.annotation.JsonAnySetter
import instacli.cli.infoString

class InputParameters {

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

