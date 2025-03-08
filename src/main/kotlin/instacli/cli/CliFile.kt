package instacli.cli

import com.fasterxml.jackson.databind.JsonNode
import instacli.language.*
import instacli.util.Yaml
import java.nio.file.Path
import kotlin.io.path.name

class CliFile(
    val cliFile: Path,
    val parsedScript: Script? = null
) : CommandInfo, CommandHandler(asScriptCommand(cliFile.name), null), AnyHandler {

    override val name: String = asCliCommand(cliFile.name)
    override val description: String by lazy { script.info?.description ?: asScriptCommand(name) }
    override val hidden: Boolean by lazy { script.info?.hidden == true }
    override val instacliSpec: String by lazy { script.info?.instacliSpec ?: "unknown" }

    val script by lazy { parsedScript ?: Script.from(scriptNodes) }
    private val scriptNodes: List<JsonNode> by lazy { Yaml.parse(cliFile) }

    override fun execute(data: JsonNode, context: ScriptContext): JsonNode? {
        val input = mutableMapOf(INPUT_VARIABLE to data)
        val localContext = CliFileContext(cliFile, context, variables = input)
        return run(localContext)
    }

    fun run(context: ScriptContext = CliFileContext(cliFile)): JsonNode? {
        return try {
            script.runScript(context)
        } catch (a: Break) {
            a.output
        } catch (e: InstacliLanguageException) {
            e.context = context.cliFile.name
            throw e
        }
    }
}