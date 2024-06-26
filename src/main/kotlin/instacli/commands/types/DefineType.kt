package instacli.commands.types

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.CommandHandler
import instacli.language.ObjectHandler
import instacli.language.ScriptContext
import instacli.language.types.TypeDefinition
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

