package instacli.cli

import com.fasterxml.jackson.annotation.JsonAnyGetter
import instacli.language.types.ObjectDefinition
import instacli.language.types.ParameterData
import kotlin.collections.contains

data class CommandLineParameters(
    @get:JsonAnyGetter
    override val properties: Map<String, ParameterData> = mutableMapOf()
) : ObjectDefinition {

    fun contains(option: String): Boolean {
        return properties.contains(option)
    }

}

