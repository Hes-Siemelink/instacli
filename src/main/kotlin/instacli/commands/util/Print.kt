package instacli.commands.util

import com.fasterxml.jackson.databind.JsonNode
import instacli.language.AnyHandler
import instacli.language.CommandHandler
import instacli.language.ScriptContext
import instacli.util.toDisplayYaml

object Print : CommandHandler("Print", "instacli/util"), AnyHandler {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {

        println(data.toDisplayYaml())

        return null
    }
}