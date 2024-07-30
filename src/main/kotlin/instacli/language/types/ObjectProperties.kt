package instacli.language.types

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.databind.JsonNode
import instacli.cli.infoString
import instacli.language.InstacliCommandError

interface ObjectDefinition {
    val properties: Map<String, PropertyDefinition>
}

fun ObjectDefinition.toDisplayString(): String {

    val builder = StringBuilder()

    val width = properties.maxOf { it.key.length } + 2
    properties.forEach {
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

data class ObjectProperties(
    @get:JsonAnyGetter
    override val properties: Map<String, PropertyData> = mutableMapOf()
) : Type, ObjectDefinition {

    override fun validate(data: JsonNode): List<String> {
        val messages = mutableListOf<String>()

        for ((field, value) in data.fields()) {
            if (field in properties.keys) {
                val parameter = properties[field]!!
                val type = BuiltinTypes.types[parameter.type.name]
                    ?: throw InstacliCommandError("Unknown type:  ${parameter.type.name}")
                messages.addAll(type.validate(value))
            }
        }

        return messages
    }
}
