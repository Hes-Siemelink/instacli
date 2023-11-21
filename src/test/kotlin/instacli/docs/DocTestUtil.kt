package instacli.docs

import instacli.cli.CliFileContext
import instacli.commands.CodeExample
import instacli.getTestName
import instacli.script.Break
import instacli.script.Script
import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicTest
import java.io.File

const val START_LINE = "```yaml"
const val END_LINE = "```"

fun readCodeBlocks(file: File): List<String> {

    val blocks = mutableListOf<String>()
    var currentBlock = mutableListOf<String>()

    var recording = false
    for (line in file.readLines()) {
        if (recording) {
            if (line == END_LINE) {
                blocks.add(currentBlock.joinToString("\n"))
                recording = false
            } else {
                currentBlock.add(line)
            }
        } else {
            if (line == START_LINE) {
                currentBlock = mutableListOf()
                recording = true
            }
        }
    }

    return blocks
}

fun getCodeExamplesInDocument(file: File): List<DynamicTest> {
    val testContext = CliFileContext(file)

    return readCodeBlocks(file)
        .map { Script.from(it) }
        .map {
            DynamicTest.dynamicTest(it.getTestName(CodeExample().name), file.toURI()) {
                try {
                    it.runScript(testContext)
                } catch (a: Break) {
                    a.output
                }
            }
        }
}

fun getCodeExamplesInAllFiles(directory: File): List<DynamicContainer> {
    val pages = directory.walkTopDown().filter { it.name.endsWith(".md") }
    return pages.mapNotNull { file ->
        DynamicContainer.dynamicContainer(file.name, getCodeExamplesInDocument(file))
    }.toList()
}

