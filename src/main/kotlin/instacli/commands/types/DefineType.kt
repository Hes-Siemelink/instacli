package instacli.commands.types

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.CommandHandler
import instacli.language.ObjectHandler
import instacli.language.ScriptContext
import instacli.language.types.ObjectProperties
import instacli.language.types.Type
import instacli.util.toDomainObject

object DefineType : CommandHandler("Type", "instacli/types"), ObjectHandler {

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

data class TypeDefinition(@JsonProperty("object") val objectProperties: ObjectProperties) : Type {
    override fun validate(data: JsonNode): List<String> {
        return objectProperties.validate(data)
    }
}
