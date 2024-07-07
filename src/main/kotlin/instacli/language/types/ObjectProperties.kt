package instacli.language.types

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.databind.JsonNode
import instacli.language.InstacliCommandError

data class ObjectProperties(
    @get:JsonAnyGetter
    val parameters: Map<String, JsonNode> = mutableMapOf()
) : Type {

    override fun validate(data: JsonNode): List<String> {
        val messages = mutableListOf<String>()

        for ((field, value) in data.fields()) {
            if (field in parameters.keys) {
                val typeInfo = parameters[field]
                val type = BuiltinTypes.types[typeInfo?.textValue()]
                    ?: throw InstacliCommandError("Unknown type:  ${typeInfo?.textValue()}")
                messages.addAll(type.validate(value))
            }
        }

        return messages
    }
}
