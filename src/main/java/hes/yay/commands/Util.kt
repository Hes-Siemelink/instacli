package hes.yay.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import hes.yay.core.*

class Task : CommandHandler("Task"), ValueHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        return null
    }
}

class Print : CommandHandler("Print"), ValueHandler, ObjectHandler {

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        println(data)
        return null
    }

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
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
            throw ScriptException("Invalid value for 'Wait' command.", data)
        }
        return null
    }
}