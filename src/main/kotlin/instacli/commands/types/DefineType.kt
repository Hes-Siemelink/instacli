package instacli.commands.types

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.cli.infoString
import instacli.language.CommandHandler
import instacli.language.ObjectHandler
import instacli.language.ScriptContext
import instacli.language.types.ObjectProperties
import instacli.language.types.ParameterData
import instacli.language.types.Type
import instacli.util.toDomainObject

object DefineType : CommandHandler("Define type", "instacli/types"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val types = data.toDomainObject(TypeDefinitions::class)

        for ((name, definition) in types.types) {
            context.registerType(name, definition)
        }

        return null
    }
}

data class TypeDefinitions(
    @get:JsonAnyGetter
    val types: Map<String, TypeDefinition> = mutableMapOf()
)

data class TypeDefinition(
    val type: String? = "object",
    val properties: ObjectProperties
) : Type {

    override fun validate(data: JsonNode): List<String> {
        return this@TypeDefinition.properties.validate(data)
    }

    fun toDisplayString(): String {

        val builder = StringBuilder()

        val width = properties.parameters.maxOf { it.key.length } + 2
        properties.parameters.forEach {
            val data = it.value.toDomainObject(ParameterData::class)
            val key = buildString {
                append("--")
                append(it.key)
                if (data.shortOption != null) {
                    append(", -")
                    append(data.shortOption)
                }
            }
            builder.append("  ")
            builder.append(infoString(key, data.description, width))
            builder.appendLine()
        }

        return builder.toString()
    }
}
