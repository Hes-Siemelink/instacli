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
        System.out.println("Parent: ${context.scriptLocation?.parent}")
        System.out.println(scriptFile.absolutePath)
        val script = YakScript.load(scriptFile)

        addVariables(script.context, data)

        script.run()

        return null
    }

}

fun addVariables(context: ScriptContext, data: JsonNode) {
    for (variable in data.fields()) {
        context.variables.put(variable.key, variable.value)
    }
}

