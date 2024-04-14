package instacli.commands.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.CommandFormatException
import instacli.language.CommandHandler
import instacli.language.ScriptContext
import instacli.language.ValueHandler

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