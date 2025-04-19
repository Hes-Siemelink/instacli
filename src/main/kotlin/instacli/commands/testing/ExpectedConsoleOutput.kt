package instacli.commands.testing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.language.CommandHandler
import instacli.language.InstacliCommandError
import instacli.language.ScriptContext
import instacli.language.ValueHandler
import instacli.util.IO.rewireSystemErr
import instacli.util.IO.rewireSystemOut
import instacli.util.Json
import instacli.util.toDisplayYaml
import java.io.ByteArrayOutputStream

object ExpectedConsoleOutput : CommandHandler("Expected console output", "instacli/testing"), ValueHandler {

    const val OUT = "stdout"
    const val ERR = "stderr"

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {

        val output = context.session[OUT]?.toString() ?: "<no output>"

        if (output.trim() != data.toDisplayYaml().trim()) {
            val error = Json.newObject()
            error.put("expected", data.toDisplayYaml().trim())
            error.put("actual", output.trim())
            throw InstacliCommandError("Output", "Unexpected console output.", error)
        }

        return null
    }

    fun storeOutput(context: ScriptContext, stdout: String, stderr: String): String {
        val out = stdout.trim()
        val err = stderr.trim()
        val combined = buildString {
            if (out.isEmpty()) {
                append(err)
            } else {
                append(out)
                if (err.isNotEmpty()) {
                    append("\n")
                    append(err)
                }
            }
        }
        context.session[OUT] = combined

        return combined
    }

    fun <T> captureSystemOutAndErr(context: ScriptContext, doThis: () -> T): T {

        val (originalOut, capturedOut) = rewireSystemOut()
        val (originalErr, capturedErr) = rewireSystemErr()

        context.session[OUT] = capturedOut
        context.session[ERR] = capturedErr

        try {
            val result = doThis()

            return result
        } finally {
            System.setOut(originalOut)
            System.setErr(originalErr)
            context.session.remove(OUT)
            context.session.remove(ERR)
        }
    }

    fun reset(context: ScriptContext) {
        (context.session[OUT] as ByteArrayOutputStream).reset()
        (context.session[ERR] as ByteArrayOutputStream).reset()
    }

}

