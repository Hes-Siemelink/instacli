package instacli.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.engine.*
import instacli.util.Yaml

class Task : CommandHandler("Task"), ValueHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return null
    }
}

// TODO Just have one method here but move error handling on CommondHandler.execute one level up
class Print : CommandHandler("Print"), ValueHandler, ObjectHandler, ArrayHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        println(data.asText())
        return null
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        println(Yaml.toString(data))
        return null
    }

    override fun execute(data: ArrayNode, context: ScriptContext): JsonNode? {
        println(Yaml.toString(data))
        return null
    }
}

class PrintAsYaml : CommandHandler("Print as YAML") {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        println(Yaml.toString(data))
        return null
    }
}

class Wait : CommandHandler("Wait"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        if (data.isNumber) {
            val duration = data.doubleValue() * 1000
            Thread.sleep(duration.toLong())
        } else {
            throw CliScriptException("Invalid value for 'Wait' command.")
        }
        return null
    }
}

class Base64Encode : CommandHandler("Base64 encode"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode {
        return TextNode(java.util.Base64.getEncoder().encodeToString(data.asText().toByteArray()))
    }
}

class Base64Decode : CommandHandler("Base64 decode"), ValueHandler {
    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return TextNode(String(java.util.Base64.getDecoder().decode(data.asText())))
    }
}