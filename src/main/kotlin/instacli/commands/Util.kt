package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.script.*
import instacli.util.toDisplayString

object Print : CommandHandler("Print"), ValueHandler, ObjectHandler, ArrayHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        println(data.asText())
        return null
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        println(data.toDisplayString())
        return null
    }

    override fun execute(data: ArrayNode, context: ScriptContext): JsonNode? {
        println(data.toDisplayString())
        return null
    }
}

object Wait : CommandHandler("Wait"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        if (!data.isNumber) {
            throw CommandFormatException("Invalid value for 'Wait' command.")
        }

        val duration = data.doubleValue() * 1000
        Thread.sleep(duration.toLong())

        return null
    }
}

object Base64Encode : CommandHandler("Base64 encode"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode {
        return TextNode(java.util.Base64.getEncoder().encodeToString(data.asText().toByteArray()))
    }
}

object Base64Decode : CommandHandler("Base64 decode"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return TextNode(String(java.util.Base64.getDecoder().decode(data.asText())))
    }
}