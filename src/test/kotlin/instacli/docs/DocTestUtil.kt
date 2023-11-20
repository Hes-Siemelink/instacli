package instacli.docs

import instacli.cli.CliFileContext
import instacli.script.Break
import instacli.script.Script
import java.io.File
import java.io.FileNotFoundException

const val START_LINE = "```yaml"
const val END_LINE = "```"

fun getDocFile(filename: String): File {
    val testDir = File("instacli-spec/commands")
    val testFile = File(testDir, filename)
    if (!testFile.exists()) {
        throw FileNotFoundException(testFile.absolutePath)
    }

    return testFile
}

fun validateCodeSnippets(file: File) {

    val codeBlocks = readCodeBlocks(file)
    val scripts = codeBlocks.map { Script.from(it) }

    scripts.forEach {
        val testContext = CliFileContext(file)
        try {
            it.runScript(testContext)
        } catch (a: Break) {
            // Exit was called from the test script
        }
    }
}

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


