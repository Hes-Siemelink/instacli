package instacli.files

import java.io.PrintStream
import java.nio.file.Path
import kotlin.io.path.readLines

class InstacliMarkdown(val document: Path) {

    val blocks = mutableListOf<Block>()

    val helperFiles: Map<String, String>
        get() = blocks
            .filter { it.type == YamlFile }
            .associate { (it.getFilename() ?: error("No file specified for ${it.getContent()}")) to it.getContent() }

    val scriptExamples: List<UsageExample>
        get() = getExamples(YamlInstacli)

    val instacliCommandExamples: List<UsageExample>
        get() = getExamples(CommandLineCli)

    val description: String? by lazy {
        // Get first main block
        val mainBlock = blocks.firstOrNull { it.type == MainText }

        // Get first lines of the block that is not a Markdown header, comment or code block
        mainBlock?.lines?.firstOrNull { line ->
            !line.startsWith("#") && !line.startsWith("<!--") && !line.startsWith("```") && line.isNotBlank()
        }
    }


    private fun getExamples(type: BlockType): List<UsageExample> {
        val all = mutableListOf<UsageExample>()
        var current: UsageExample? = null
        var before: String? = null
        for (block in blocks) {
            when (block.type) {
                type -> {
                    current = UsageExample(
                        block.getContent(),
                        directory = block.getDirectory()?.toPath(),
                        before = before
                    )
                    all.add(current)
                    before = null
                }

                Input -> {
                    current?.input = block.getContent()
                }

                Output -> {
                    current?.output = block.getContent()
                }

                YamlInstacliBefore -> {
                    before = block.getContent() + "\n"
                }

                YamlInstacliAfter -> {
                    current ?: continue
                    current.after = "\n" + block.getContent()
                }

                // Clean up context when encountering a block of a different type
                CommandLineCli, YamlInstacli, YamlFile -> {
                    current = null
                    before = null
                }
            }
        }

        return all
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
            YamlInstacliBefore,
            YamlInstacli,
            YamlInstacliAfter,
            CommandLineCli,
            CommandLine,
            Input,
            Output,
            MainText // Should be last
        )

        fun scan(document: Path): InstacliMarkdown {

            val doc = InstacliMarkdown(document)
            var currentBlock = doc.addBlock(MainText)
            for (line in document.readLines()) {
                when {
                    currentBlock.type == MainText -> {
                        val startBlockType = blockTypes.firstOrNull {
                            line.startsWith(it.firstLinePrefix)
                        }

                        if (startBlockType == null || startBlockType == MainText) {
                            currentBlock.lines.add(line)
                        } else {
                            currentBlock = doc.addBlock(startBlockType, line)
                        }
                    }

                    line.startsWith(currentBlock.type.lastLinePrefix) -> {
                        currentBlock = doc.addBlock(MainText)
                    }

                    else -> {
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
object YamlInstacliBefore : BlockType("<!-- yaml instacli before", "-->")
object YamlInstacliAfter : BlockType("<!-- yaml instacli after", "-->")
object YamlInstacli : BlockType("```yaml instacli")
object YamlFile : BlockType("```yaml file")
object CommandLineCli : BlockType("```commandline cli")
object CommandLine : BlockType("```commandline")
object Input : BlockType("<!-- input", "-->")
object Output : BlockType("```output")

data class UsageExample(
    val command: String,
    val directory: Path? = null,
    var input: String? = null,
    var output: String? = null,
    var before: String? = null,
    var after: String? = null
) {
    val content: String
        get() = (before ?: "") + command + (after ?: "")
}
