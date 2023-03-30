package hes.yak

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import hes.yak.commands.*
import hes.yak.http.HttpEndpoint
import hes.yak.http.HttpGet

abstract class CommandHandler(val name: String) {

    open fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        if (data is ValueNode) {
            return if (this is ValueHandler) {
                execute(data, context)
            } else {
                throw ScriptException("Command '$name' does not handle simple text content.", data)
            }
        } else if (data is ObjectNode) {
            return if (this is ObjectHandler) {
                execute(data, context)
            } else {
                throw ScriptException("Command '$name' does not handle object content.", data)
            }
        } else if (data is ArrayNode) {
            return if (this is ArrayHandler) {
                execute(data, context)
            } else {
                throw ScriptException("Command '$name' does not handle array content.", data)
            }
        }

        throw ScriptException("Unknown content type ${data.javaClass.simpleName} for command '$name'", data)
    }
}


interface ValueHandler {
    abstract fun execute(data: ValueNode, context: ScriptContext): JsonNode?
}
interface ObjectHandler {
    abstract fun execute(data: ObjectNode, context: ScriptContext): JsonNode?
}
interface ArrayHandler {
    abstract fun execute(data: ArrayNode, context: ScriptContext): JsonNode?
}

interface DelayedVariableResolver

//
// Helper functions
//

fun objectNode(key: String, value: String): ObjectNode {
    return ObjectNode(JsonNodeFactory.instance).put(key, value)
}

//
// Core library
//

object Core {
    val commands = commandMap(
        TestCase(),
        AssertEquals(),
        AssertThat(),
        ExpectedOutput(),
        Input(),
        Output(),
        ExecuteYayFile(),
        Do(),
        ForEach(),
        Join(),
        AssignOutput(),
        Merge(),
        Print(),
        ReadFile(),
        ApplyVariables(),
        Repeat(),
        If(),
        IfAny(),
        HttpEndpoint(),
        HttpGet()
    )

    private fun commandMap(vararg commands: CommandHandler): Map<String, CommandHandler> {
        return commands.associateBy { it.name }
    }
}