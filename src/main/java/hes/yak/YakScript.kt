package hes.yak

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import hes.yak.commands.*
import java.io.File

class YakScript(
    val script: List<JsonNode>,
    val context: ScriptContext = ScriptContext()) {

    fun run() {
        for (block in script) {
            runTaskBlock(block, context)
        }
    }

    private fun runTaskBlock(block: JsonNode, context: ScriptContext) {
        for (command in block.fields()) {
            if (command.key in commands.keys) {
                commands.get(command.key)!!.execute(command.value, context);
            } else {
                throw ScriptException("Unknown command: ${command.key}")
            }
        }
    }

    companion object {

        val commands: MutableMap<String, Command> = mutableMapOf()

        private val factory = YAMLFactory()
        private val mapper = ObjectMapper(factory).registerKotlinModule()

        init {
            commands.put("Test case", TestCase())
            commands.put("Assert equals", AssertEquals())
            commands.put("Assert that", AssertThat())
            commands.put("Input", Input())
            commands.put("Output", Output())
            commands.put("Execute yay file", ExecuteYayFile())
        }

        fun load(
            source: File,
            scriptContext: ScriptContext = ScriptContext()
        ): YakScript {

            val yamlParser = factory.createParser(source)
            val script = mapper
                .readValues(yamlParser, JsonNode::class.java)
                .readAll();
            scriptContext.scriptLocation = source

            return YakScript(script, scriptContext)
        }

        fun load(
            resource: String,
            scriptContext: ScriptContext = ScriptContext()): YakScript {

            val resource = YakScript::class.java.getResource(resource)
            scriptContext.scriptLocation = File(resource.file)

            val yamlParser = factory.createParser(resource.readText())
            val script = mapper
                .readValues(yamlParser, JsonNode::class.java)
                .readAll();

            return YakScript(script, scriptContext)
        }

        fun run(resource: String) {
            load(resource).run()
        }

    }
}

class ScriptException(message: String) : Exception(message)