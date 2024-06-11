package instacli.commands.datamanipulation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import instacli.language.*
import instacli.util.applyPatch

object JsonPatch : CommandHandler("Json patch", "instacli/data-manipulation"), ObjectHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode {

        val doc = data["doc"]
            ?: context.output
            ?: throw InstacliCommandError("Json patch needs 'doc' parameter or non-null output variable.")
        val patch = data.getParameter("patch") as ArrayNode

        return doc.applyPatch(patch)
    }
}