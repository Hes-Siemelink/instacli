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
            if (command.key !in commands.keys) {
                throw ScriptException("Unknown command: ${command.key}")
            }

            output = commands.get(command.key)!!.execute(command.value, context);
            context.output = output
        }

        return output
    }

    companion object {

        val commands: MutableMap<String, Command> = mutableMapOf()

        private val factory = YAMLFactory()
        private val mapper = ObjectMapper(factory).registerKotlinModule()

        init {
            commands["Test case"] = TestCase()
            commands["Assert equals"] = AssertEquals()
            commands["Assert that"] = AssertThat()
            commands["Expected output"] = ExpectedOutput()
            commands["Input"] = Input()
            commands["Output"] = Output()
            commands["Execute yay file"] = ExecuteYayFile()
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

        // TODO: Move resource loading to test classes and just deal with files here
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