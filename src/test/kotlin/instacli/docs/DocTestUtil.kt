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
import java.nio.file.Files

fun getCodeExamplesInDocument(file: File): List<DynamicTest> {

    // Scan document for code examples
    val doc = InstacliDoc(file)
    doc.scan()

    // Set up test dir with helper files from document
    val testDir = Files.createTempDirectory("instacli-").toFile()
    doc.helperFiles.forEach {
        File(testDir, it.key).writeText(it.value)
    }

    // Generate tests
    return doc.codeExamples
        .map {
            val script = Script.from(it)
            val testContext = CliFileContext(testDir)
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

