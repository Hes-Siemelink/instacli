package instacli.commands.datamanipulation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import instacli.language.*

object Size : CommandHandler("Size", "instacli/data-manipulation"), ValueHandler, ArrayHandler, ObjectHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {

        when (data) {
            is NumericNode -> {
                return data
            }

            is BooleanNode -> {
                return if (data.booleanValue()) IntNode(1) else IntNode(0)
            }

            is TextNode -> {
                return IntNode(data.textValue().length)
            }
        }

        throw CommandFormatException("Unsupported type: ${data.javaClass}")
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        return IntNode(data.size())
    }

    override fun execute(data: ArrayNode, context: ScriptContext): JsonNode? {
        return IntNode(data.size())
    }
}