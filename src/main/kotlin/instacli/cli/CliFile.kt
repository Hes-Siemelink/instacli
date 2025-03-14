package instacli.cli

import com.fasterxml.jackson.databind.JsonNode
import instacli.doc.InstacliMarkdown
import instacli.language.*
import instacli.util.Yaml
import java.nio.file.Path
import kotlin.io.path.name

class CliFile(val file: Path) : CommandInfo, CommandHandler(asScriptCommand(file.name), null), AnyHandler {

    override val name: String = asCliCommand(file.name)
    override val description: String by lazy { script.info?.description ?: asScriptCommand(name) }
    override val hidden: Boolean by lazy { script.info?.hidden == true }
    override val instacliSpec: String by lazy { script.info?.instacliSpec ?: "unknown" }

    val script by lazy { Script.from(scriptNodes) }
    private val scriptNodes: List<JsonNode> by lazy {
        if (file.name.endsWith(CLI_MARKDOWN_SCRIPT_EXTENSION)) {
            val document = InstacliMarkdown.scan(file)
            document.instacliYamlBlocks.map { Yaml.parse(it) }
        } else {
            Yaml.parse(file)
        }
    }

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        val input = mutableMapOf(INPUT_VARIABLE to data)
        val localContext = CliFileContext(file, context, variables = input)

        return script.run(localContext)
    }

    fun run(context: ScriptContext = CliFileContext(file)): JsonNode? {
        return script.run(context)
    }
}