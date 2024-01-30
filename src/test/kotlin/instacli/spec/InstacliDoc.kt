package instacli.spec

import java.nio.file.Path
import kotlin.io.path.readLines

class InstacliDoc(val document: Path) {

    private val blocks = mutableListOf<Block>()

    val codeExamples: List<String>
        get() {
            val scripts = mutableListOf<String>()
            var beforeBuffer = ""

            for (block in blocks) {
                if (block.type == YamlSnippet) {
                    scripts.add(beforeBuffer + "\n" + block.getContent())
                    beforeBuffer = ""
                } else if (block.type == HiddenCode) {
                    beforeBuffer += "\n" + block.getContent()
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

    fun get(type: BlockType): List<Block> {
        return blocks.filter { it.type == type }
    }

    private fun addBlock(type: BlockType, headerLine: String = ""): Block {
        val block = Block(type, headerLine)
        blocks.add(block)
        return block
    }

    companion object {

        private val blockTypes: List<BlockType> = listOf(
            HiddenCode,
            YamlFile,  // Should come before YamlSnippet
            YamlSnippet,
            CliInvocation,
            CliOutput,
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

open class BlockType(val firstLinePrefix: String = "", val lastLinePrefix: String = "```")

val FILE_REGEX = Regex("file:(\\S+)")

class Block(val type: BlockType, val headerLine: String = "", val lines: MutableList<String> = mutableListOf()) {
    fun getFilename(): String? {
        val fileMatch = FILE_REGEX.find(headerLine)
        return fileMatch?.groupValues?.get(1)
    }

    fun getContent(): String {
        return lines.joinToString("\n")
    }
}

object MainText : BlockType()
object HiddenCode : BlockType("<!-- run before", "-->")
object YamlSnippet : BlockType("```yaml")
object YamlFile : BlockType("```yaml file")
object CliInvocation : BlockType("```commandline cli")
object CliOutput : BlockType("```output")

