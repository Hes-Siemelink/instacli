package hes.yak

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import hes.yak.commands.AssertEquals
import hes.yak.commands.AssertThat
import hes.yak.commands.TestCase


class YakScript(val contents: String) {


    fun run() {
        val factory = YAMLFactory()
        val mapper = ObjectMapper(factory).registerKotlinModule()

        val yamlParser = factory.createParser(contents)

        val script = mapper
            .readValues(yamlParser, JsonNode::class.java)
            .readAll();

        for (node in script) {
            run(node)
        }
    }

    fun run(node: JsonNode) {
        for (field in node.fields()) {
            if (field.key in commands.keys) {
                commands.get(field.key)!!.execute(field.value);
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
        }

        fun load(resource: String): YakScript {
            val raw = this::class.java.getResource(resource).readText()
            return YakScript(raw)
        }
    }
}

