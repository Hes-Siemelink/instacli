package instacli.files

import instacli.files.MarkdownBlock.*
import java.io.PrintStream
import java.nio.file.Path
import kotlin.io.path.readLines

class InstacliMarkdown(val document: Path) {

    val blocks = mutableListOf<MarkdownBlock>()

    val helperFiles: Map<String, String>
        get() = blocks
            .filter { it.type == YamlFile }
            .associate { (it.getFilename() ?: error("No file specified for ${it.getContent()}")) to it.getContent() }

    val description: String? by lazy {
        // Get first text block
        val mainBlock = blocks.firstOrNull { it.type == Text && it.lines.isNotEmpty() }

        // Get first lines of the block that is not a Markdown header, comment or code block
        mainBlock?.lines?.firstOrNull { line ->
            !line.startsWith("#") && !line.startsWith("<!--") && !line.startsWith("```") && line.isNotBlank()
        }
    }

    fun get(type: BlockType): List<MarkdownBlock> {
        return blocks.filter { it.type == type }
    }

    private fun addBlock(type: BlockType, headerLine: String = ""): MarkdownBlock {
        val block = MarkdownBlock(type, headerLine)
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
            ShellCli,
            ShellBlock,
            Input,
            Output,
            Header,
            Text // Should be last
        )

        fun scan(document: Path): InstacliMarkdown {

            val doc = InstacliMarkdown(document)
            var currentBlock = doc.addBlock(Text)
            for (line in document.readLines()) {
                when {
                    currentBlock.type == Text -> {
                        val startBlockType = blockTypes.firstOrNull {
                            line.startsWith(it.firstLinePrefix)
                        }

                        if (startBlockType == null || startBlockType == Text) {
                            currentBlock.lines.add(line)
                        } else {
                            currentBlock = doc.addBlock(startBlockType, line)
                        }
                    }

                    line.startsWith(currentBlock.type.lastLinePrefix) -> {
                        currentBlock = doc.addBlock(Text)
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


open class BlockType(val firstLinePrefix: String, val lastLinePrefix: String)

val FILE_REGEX = Regex("file:(\\S+)")
val DIRECTORY_REGEX = Regex("directory:(\\S+)")

class MarkdownBlock(
    val type: BlockType,
    val headerLine: String = "",
    val lines: MutableList<String> = mutableListOf()
) {
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

    object Text : BlockType("", "```")
    object Header : BlockType("#", "")
    object YamlInstacliBefore : BlockType("<!-- yaml instacli before", "-->")
    object YamlInstacliAfter : BlockType("<!-- yaml instacli after", "-->")
    object YamlInstacli : BlockType("```yaml instacli", "```")
    object YamlFile : BlockType("```yaml file", "```")
    object ShellCli : BlockType("```shell cli", "```")
    object ShellBlock : BlockType("```shell", "```")
    object Input : BlockType("<!-- input", "-->")
    object Output : BlockType("```output", "```")
}
