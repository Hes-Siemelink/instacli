package instacli.cli

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import instacli.commands.toPath
import instacli.script.*
import instacli.util.Yaml
import instacli.util.objectNode
import java.nio.file.Path
import kotlin.io.path.name

class CliFile(val cliFile: Path) : CommandInfo, CommandHandler(asScriptCommand(cliFile.name)), AnyHandler {

    override val name: String = asCliCommand(cliFile.name)
    override val description: String by lazy { script.info?.description ?: asScriptCommand(name) }

    val script by lazy { Script.from(scriptNodes) }
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
        } catch (e: InstacliException) {
            e.context = context.cliFile.name
            throw e
        }
    }
}

object RunScript : CommandHandler("Run script"), ObjectHandler, ValueHandler {

    override fun execute(data: ObjectNode, context: ScriptContext): JsonNode? {
        val file = data.toPath(context)
        val input = data["input"] ?: objectNode()

        return handleCommand(CliFile(file), input, context)
    }

    override fun execute(data: ValueNode, context: ScriptContext): JsonNode? {
        val file = data.toPath(context)

        return handleCommand(CliFile(file), objectNode(), context)
    }
}