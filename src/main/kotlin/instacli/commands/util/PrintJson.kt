package instacli.commands.util

import com.fasterxml.jackson.databind.JsonNode
import instacli.language.AnyHandler
import instacli.language.CommandHandler
import instacli.language.ScriptContext
import instacli.util.toDisplayJson

object PrintJson : CommandHandler("Print JSON", "instacli/util"), AnyHandler {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {

        println(data.toDisplayJson())

        return null
    }
}