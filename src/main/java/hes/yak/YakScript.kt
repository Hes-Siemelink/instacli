package hes.yak

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import hes.yak.commands.*

class YakScript(val script: List<JsonNode>) {

    fun run() {

        val context = ScriptContext()

        for (node in script) {
            run(node, context)
        }
    }

    private fun run(node: JsonNode, context: ScriptContext) {
        for (field in node.fields()) {
            if (field.key in commands.keys) {
                commands.get(field.key)!!.execute(field.value, context);
            } else {
                throw ScriptException("Unknown command: ${field.key}")
            }
        }
    }

    companion object {

        val commands: MutableMap<String, Command> = mutableMapOf()

        init {
            commands.put("Test case", TestCase())
            commands.put("Assert equals", AssertEquals())
            commands.put("Assert that", AssertThat())
            commands.put("Input", Input())
            commands.put("Output", Output())
        }

        fun load(resource: String): YakScript {
            val raw = YakScript::class.java.getResource(resource).readText()
            val factory = YAMLFactory()
            val mapper = ObjectMapper(factory).registerKotlinModule()

            val yamlParser = factory.createParser(raw)

            val script = mapper
                .readValues(yamlParser, JsonNode::class.java)
                .readAll();

            return YakScript(script)
        }

        fun run(resource: String) {
            load(resource).run()
        }

    }
}

class ScriptException(message: String) : Exception(message)