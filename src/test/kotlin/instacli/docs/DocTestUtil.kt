package instacli.docs

import instacli.cli.CliFileContext
import instacli.commands.CodeExample
import instacli.commands.userPrompt
import instacli.getTestName
import instacli.script.Break
import instacli.script.Script
import instacli.util.MockUser
import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicTest
import java.io.File

const val START_RUN_BEFORE_CODE_EXAMPLE = "<!-- run before code example"
const val END_RUN_BEFORE_CODE_EXAMPLE = "-->"
const val START_CODE_EXAMPLE = "```yaml"
const val END_CODE_EXAMPLE = "```"

fun readCodeBlocks(file: File): List<String> {

    val blocks = mutableListOf<String>()
    val currentBlock = mutableListOf<String>()

    var recording = false
    for (line in file.readLines()) {
        if (recording) {
            when (line) {
                END_RUN_BEFORE_CODE_EXAMPLE -> {
                    recording = false
                }

                END_CODE_EXAMPLE -> {
                    blocks.add(currentBlock.joinToString("\n"))
                    currentBlock.clear()
                    recording = false
                }

                else -> {
                    currentBlock.add(line)
                }
            }
        } else {
            if (line == START_CODE_EXAMPLE || line == START_RUN_BEFORE_CODE_EXAMPLE) {
                recording = true
            }
        }
    }

    return blocks
}

fun getCodeExamplesInDocument(file: File): List<DynamicTest> {

    return readCodeBlocks(file)
        .map {
            val script = Script.from(it)
            val testContext = CliFileContext(file)
            userPrompt = MockUser()
            DynamicTest.dynamicTest(script.getTestName(CodeExample().name), file.toURI()) {
                try {
                    script.runScript(testContext)
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

