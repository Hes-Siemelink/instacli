package instacli.files

import com.fasterxml.jackson.databind.JsonNode
import instacli.language.*
import instacli.util.Yaml
import java.nio.file.Path
import kotlin.io.path.name

class CliFile(val file: Path) : CommandInfo, CommandHandler(asScriptCommand(file.name), null), AnyHandler {

    override val name: String = asCliCommand(file.name)
    override val description: String by lazy {
        markdown?.description
            ?: script.info?.description
            ?: asScriptCommand(name)
    }
    override val hidden: Boolean by lazy { script.info?.hidden == true }
    override val instacliSpec: String by lazy { script.info?.instacliSpec ?: "unknown" }

    val script by lazy {
        markdown?.blocks?.toScript()
            ?: Script.from(scriptNodes)
    }
    private val scriptNodes: List<JsonNode> by lazy { Yaml.parse(file) }

    val markdown: InstacliMarkdown? by lazy {
        if (file.isMarkdownScript()) {
            InstacliMarkdown.scan(file)
        } else {
            null
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

private fun Path.isMarkdownScript(): Boolean {
    return this.name.endsWith(".md")
}

fun CliFile.splitMarkdown(): List<Script> {

    val document = markdown ?: return listOf()

    val all = mutableListOf<List<MarkdownBlock>>()
    var currentCase = mutableListOf<MarkdownBlock>()

    for (block in document.blocks) {
        when (block.type) {
            MarkdownBlock.Header -> {
                // Add previous case
                if (currentCase.isNotEmpty()) {
                    all.add(currentCase)
                }
                currentCase = mutableListOf(block)
            }

            else -> {
                currentCase.add(block)
            }
        }
    }

    // Add the last one
    all.add(currentCase)

    return all.map { it.toScript() }
}
