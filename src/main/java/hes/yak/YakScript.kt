package hes.yak

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import hes.yak.commands.*
import java.io.File

class YakScript(
    private val script: List<JsonNode>,
    val context: ScriptContext = ScriptContext()) {

    fun run(): JsonNode? {
        var output: JsonNode? = null

        for (block in script) {
            output = runTaskBlock(block, context)
        }

        return output
    }

    private fun runTaskBlock(block: JsonNode, context: ScriptContext): JsonNode? {
        var output: JsonNode? = null

        for (command in block.fields()) {
            val handler = context.getCommandHandler(command.key)
            output = runCommand(handler, command.value, context)

            context.output = output
        }

        return output
    }

    private fun runCommand(
        handler: Command,
        rawData: JsonNode,
        context: ScriptContext
    ): JsonNode? {

        val data = resolveVariables(rawData, context.variables)

        return handler.execute(data, context)
    }


    companion object {

        private val factory = YAMLFactory()
        private val mapper = ObjectMapper(factory).registerKotlinModule()

        fun run(script: File) {
            load(script).run()
        }

        fun load(
            source: File,
            scriptContext: ScriptContext = ScriptContext()
        ): YakScript {

            val yamlParser = factory.createParser(source)
            val script = mapper
                .readValues(yamlParser, JsonNode::class.java)
                .readAll()
            scriptContext.scriptLocation = source

            return YakScript(script, scriptContext)
        }
    }
}

class ScriptException(message: String) : Exception(message)