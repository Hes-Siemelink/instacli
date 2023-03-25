package hes.yak.commands

import com.fasterxml.jackson.databind.JsonNode
import hes.yak.Command
import hes.yak.ScriptContext
import hes.yak.ScriptException
import hes.yak.YakScript
import java.io.File

class ExecuteYayFile : Command {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        val fileName = data.get("file") ?: throw ScriptException("Execute yay file needs 'file' field.")
        val scriptFile = File(context.scriptLocation?.parent, fileName.asText())

        return runFile(scriptFile, data)
    }

}

class ExecuteYayFileAsCommand(private val scriptFile: File) : Command {

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        return runFile(scriptFile, data)
    }
}

private fun runFile(scriptFile: File, data: JsonNode): JsonNode? {
    val script = YakScript.load(scriptFile)
    for (variable in data.fields()) {
        script.context.variables[variable.key] = variable.value
    }

    return script.run()
}

