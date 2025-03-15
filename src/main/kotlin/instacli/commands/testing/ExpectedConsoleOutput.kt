package instacli.commands.testing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.CommandHandler
import instacli.language.ScriptContext
import instacli.language.ValueHandler
import instacli.util.toDisplayYaml

object ExpectedConsoleOutput : CommandHandler("Expected console output", "instacli/testing"), ValueHandler {

    const val OUTPUT = "stdout"

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {

        val output = context.session[OUTPUT]?.toString() ?: "<no output>"

        if (output.trim() != data.toDisplayYaml().trim()) {
            throw AssertionError("Unexpected output.\nExpected:<${data.toDisplayYaml()}>\nOutput:   <$output>")
        }

        return null
    }

}