package instacli.commands.datamanipulation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.*

object Find : CommandHandler("Find", "instacli/data-manipulation"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {

        val path = data.getTextParameter("path")
        val source = data.getParameter("in")

        return source.at(toJsonPointer(path))
    }
}