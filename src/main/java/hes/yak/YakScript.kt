package hes.yak

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

class YakScript(
    private val script: List<JsonNode>,
    val context: ScriptContext = ScriptContext()) {

    fun run(): JsonNode? {
        val statements = script.map { scriptNode -> toStatements(scriptNode) }.flatten()
        return runScript(statements, context)
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