package hes.yak

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
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
        }

        return output
    }

    fun runCommand(
        handler: Command,
        rawData: JsonNode,
        context: ScriptContext
    ): JsonNode? {

        if (handler is ListProcessor && rawData is ArrayNode) {
            return runCommandOnList(handler, rawData, context)
        } else {
            return runSingleCommand(handler, rawData, context)
        }
    }

    private fun runCommandOnList(
        handler: Command,
        dataList: ArrayNode,
        context: ScriptContext
    ): ArrayNode? {

        val output = ArrayNode(JsonNodeFactory.instance)

        for (data in dataList) {
            val result = runSingleCommand(handler, data, context)
            if (result != null) {
                output.add(result)
            }
        }

        return if (output.isEmpty()) {
            null
        } else {
            context.variables["output"] = output
            output
        }
    }

    private fun runSingleCommand(
        handler: Command,
        rawData: JsonNode,
        context: ScriptContext
    ): JsonNode? {

        val data = if (handler is DelayedVariableResolver) rawData else resolveVariables(rawData, context.variables)
        val result: JsonNode? = handler.execute(data, context)
        if (result != null) {
            context.variables["output"] = result
        }
        return result
    }

    companion object {

        private val factory = YAMLFactory()
        val mapper = ObjectMapper(factory).registerKotlinModule()

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