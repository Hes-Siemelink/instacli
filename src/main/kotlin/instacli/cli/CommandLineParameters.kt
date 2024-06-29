package instacli.cli

import com.fasterxml.jackson.annotation.JsonAnyGetter
import instacli.language.types.ParameterData
import instacli.language.types.TypeDefinition
import instacli.util.toDomainObject
import java.lang.StringBuilder
import kotlin.collections.contains
import kotlin.collections.forEach
import kotlin.collections.maxOf
import kotlin.text.appendLine

data class CommandLineParameters(
    @get:JsonAnyGetter
    val parameters: Map<String, ParameterData> = mutableMapOf()
) {

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

    companion object {
        fun from(type: TypeDefinition): CommandLineParameters {
            val transformedParameters: Map<String, ParameterData> = type.properties.parameters.mapValues { parameter ->
                parameter.value.toDomainObject(ParameterData::class)
            }

            return CommandLineParameters(transformedParameters)
        }
    }
}

