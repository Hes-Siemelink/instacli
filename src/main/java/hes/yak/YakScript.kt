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

        private val factory = YAMLFactory()
            .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
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

class ScriptException(message: String, val data: JsonNode? = null) : Exception(message) {
    override val message: String?
        get() = if (data != null) {
            "${super.message}\n${YakScript.mapper.writeValueAsString(data)}"
        } else {
            super.message
        }
}