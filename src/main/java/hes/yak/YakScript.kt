package hes.yak

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

class YakScript(
    private val script: List<JsonNode>,
    val context: ScriptContext = ScriptContext()) {

    fun run(): JsonNode? {
        val statements = script.map { scriptNode -> toCommands(scriptNode) }.flatten()
        return runScript(statements, context)
    }

    companion object {

        fun run(script: File) {
            load(script).run()
        }

        fun load(
            source: File,
            scriptContext: ScriptContext = ScriptContext()
        ): YakScript {

            val script = Yaml.parse(source)
            scriptContext.scriptLocation = source

            return YakScript(script, scriptContext)
        }

    }
}

class Yaml {

    companion object {
        private val factory = YAMLFactory()
            .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        private val mapper = ObjectMapper(factory).registerKotlinModule()

        fun readFile(textValue: String): JsonNode? {
            return mapper.readValue(File(textValue), JsonNode::class.java)
        }

        fun parse(source: File): List<JsonNode> {
            val yamlParser = Yaml.factory.createParser(source)
            return Yaml.mapper
                .readValues(yamlParser, JsonNode::class.java)
                .readAll()
        }

        fun parse(source: String): JsonNode {
            return Yaml.mapper.readValue(source, JsonNode::class.java)
        }

        fun toString(node: JsonNode): String {
            return mapper.writeValueAsString(node)
        }
    }
}

class ScriptException(message: String, val data: JsonNode? = null) : Exception(message) {
    override val message: String?
        get() = if (data != null) {
            "${super.message}\n${Yaml.toString(data)}"
        } else {
            super.message
        }
}