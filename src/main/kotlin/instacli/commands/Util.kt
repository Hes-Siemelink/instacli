package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.*
import instacli.util.toDisplayYaml

object Print : CommandHandler("Print", "instacli/util"), AnyHandler {
    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        println(data.toDisplayYaml())
        return null
    }
}

object Wait : CommandHandler("Wait", "instacli/util"), ValueHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {

        if (!data.isNumber) {
            throw CommandFormatException("Invalid value for 'Wait' command.")
        }

        val duration = data.doubleValue() * 1000
        Thread.sleep(duration.toLong())

        return null
    }
}

object Base64Encode : CommandHandler("Base64 encode", "instacli/util"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode {
        return TextNode(java.util.Base64.getEncoder().encodeToString(data.asText().toByteArray()))
    }
}

object Base64Decode : CommandHandler("Base64 decode", "instacli/util"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return TextNode(String(java.util.Base64.getDecoder().decode(data.asText())))
    }
}