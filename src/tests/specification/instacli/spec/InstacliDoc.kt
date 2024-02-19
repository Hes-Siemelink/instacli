package instacli.spec

import instacli.util.addNotNull
import java.io.PrintStream
import java.nio.file.Path
import kotlin.io.path.readLines

class InstacliDoc(val document: Path) {

    private val blocks = mutableListOf<Block>()

    val codeExamples: List<String>
        get() {
            val scripts = mutableListOf<String>()
            var beforeBuffer = ""

            for (block in blocks) {
                when (block.type) {
                    YamlScriptBefore -> {
                        beforeBuffer += block.getContent() + "\n"
                    }

                    YamlScript -> {
                        scripts.add(beforeBuffer + block.getContent())
                        beforeBuffer = ""
                    }

                    YamlScriptAfter -> {
                        if (scripts.size == 0) {
                            continue
                        }
                        scripts[scripts.size - 1] = scripts[scripts.size - 1] + "\n" + block.getContent()
                    }
                }
            }
            return scripts
        }

    val helperFiles: Map<String, String>
        get() {
            return blocks.filter { it.type == YamlFile }
                .associate {
                    (it.getFilename() ?: error("No file specified for ${it.getContent()}")) to it.getContent()
                }
        }

    val commandExamples: List<CommandExample>
        get() {
            val commands = mutableListOf<CommandExample>()
            var currentCommand: CommandExample? = null
            for (block in blocks) {
                when (block.type) {
                    CommandInvocation -> {
                        commands.addNotNull(currentCommand)
                        currentCommand = CommandExample(block.getContent(), directory = block.getDirectory()?.toPath())
                    }

                    CommandInput -> {
                        currentCommand?.input = block.getContent()
                    }

                    CommandOutput -> {
                        currentCommand?.output = block.getContent()
                    }
                }
            }
            commands.addNotNull(currentCommand)

            return commands
        }

    fun get(type: BlockType): List<Block> {
        return blocks.filter { it.type == type }
    }

    private fun addBlock(type: BlockType, headerLine: String = ""): Block {
        val block = Block(type, headerLine)
        blocks.add(block)
        return block
    }

    fun print(out: PrintStream) {
        blocks.forEach {
            it.print(out)
        }
    }

    companion object {

        private val blockTypes: List<BlockType> = listOf(
            YamlFile,
            YamlScriptBefore,
            YamlScript,
            YamlScriptAfter,
            CommandInvocation,
            CommandInput,
            CommandOutput,
            MainText // Should be last
        )

        fun scan(document: Path): InstacliDoc {

            val doc = InstacliDoc(document)
            var currentBlock = doc.addBlock(MainText)
            for (line in document.readLines()) {
                if (currentBlock.type == MainText) {
                    val startBlockType = blockTypes.firstOrNull {
                        line.startsWith(it.firstLinePrefix)
                    }

                    if (startBlockType == null || startBlockType == MainText) {
                        currentBlock.lines.add(line)
                    } else {
                        currentBlock = doc.addBlock(startBlockType, line)
                    }
                } else {
                    if (line.startsWith(currentBlock.type.lastLinePrefix)) {
                        currentBlock = doc.addBlock(MainText)
                    } else {
                        currentBlock.lines.add(line)
                    }
                }
            }

            return doc
        }
    }
}

private fun String.toPath(): Path {
    return Path.of(this)
}

open class BlockType(val firstLinePrefix: String = "", val lastLinePrefix: String = "```")

val FILE_REGEX = Regex("file:(\\S+)")
val DIRECTORY_REGEX = Regex("directory:(\\S+)")

class Block(val type: BlockType, val headerLine: String = "", val lines: MutableList<String> = mutableListOf()) {
    fun getFilename(): String? {
        val fileMatch = FILE_REGEX.find(headerLine)
        return fileMatch?.groupValues?.get(1)
    }

    fun getDirectory(): String? {
        val fileMatch = DIRECTORY_REGEX.find(headerLine)
        return fileMatch?.groupValues?.get(1)
    }

    fun getContent(): String {
        return lines.joinToString("\n")
    }

    fun print(out: PrintStream) {
        if (headerLine.isNotEmpty()) {
            out.println(headerLine)
        }

        lines.forEach {
            out.println(it)
        }

        if (headerLine.isNotEmpty()) {
            out.println(type.lastLinePrefix)
        }
    }
}

object MainText : BlockType()
object YamlScriptBefore : BlockType("<!-- yaml instacli before", "-->")
object YamlScriptAfter : BlockType("<!-- yaml instacli after", "-->")
object YamlScript : BlockType("```yaml instacli")
object YamlFile : BlockType("```yaml file")
object CommandInvocation : BlockType("```commandline cli")
object CommandInput : BlockType("<!-- cli input", "-->")
object CommandOutput : BlockType("```cli output")

data class CommandExample(
    val command: String,
    var output: String? = null,
    var input: String? = null,
    val directory: Path? = null
)