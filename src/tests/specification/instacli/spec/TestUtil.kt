package instacli.spec

import instacli.cli.reportError
import instacli.commands.testing.CodeExample
import instacli.commands.testing.TestCase
import instacli.commands.userinteraction.TestPrompt
import instacli.commands.userinteraction.UserPrompt
import instacli.files.*
import instacli.language.*
import instacli.util.toDisplayYaml
import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.function.Executable
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

//
// All
//

fun Path.getTests(): List<DynamicNode> {

    UserPrompt.default = TestPrompt

    if (isDirectory()) {
        return listDirectoryEntries().mapNotNull {
            val tests = it.getTests()
            if (tests.isEmpty()) {
                null
            } else {
                dynamicContainer(it.name, tests)
            }
        }
    } else {
        if (name.endsWith(CLI_SCRIPT_EXTENSION)) {
            return CliFile(this).getTestCases()
        } else if (name.endsWith(MARKDOWN_SPEC_EXTENSION)) {
            return CliFile(this).getCodeExamples()
        }
    }
    return emptyList()
}

class TestCaseRunner(
    val context: ScriptContext,
    val script: Script
) : Executable {

    override fun execute() {
        context.error = null
        try {
            script.run(context)
        } catch (a: Break) {
            a.output
        } catch (e: InstacliCommandError) {
            e.error.data?.let {
                System.err.println(it.toDisplayYaml())
            }
            throw e
        } catch (e: InstacliLanguageException) {
            e.reportError(printStackTrace = false)
            throw e
        }
    }
}

/**
 * Extracts the test cases from a script file as individual tests.
 */
fun CliFile.getTestCases(): List<DynamicTest> {
    val context = CliFileContext(file)

    return script.splitTestCases().map { script ->
        dynamicTest(script.getTestTitle(TestCase), file.toUri(), TestCaseRunner(context, script))
    }
}

/**
 * Extracts the yaml code from Markdown sections as individual tests.
 */
fun CliFile.getCodeExamples(): List<DynamicTest> {

    // Set up test dir
    val testDir = Files.createTempDirectory("instacli-")
    testDir.toFile().deleteOnExit()
    val context = CliFileContext(testDir)
    context.setTempDir(testDir)

    val scripts = splitMarkdown()
    val instacliTests: List<DynamicTest> = scripts
        .mapNotNull {
            toTestFromScript(file, it, context)
        }

    return instacliTests
}

private fun toTestFromScript(
    document: Path,
    script: Script,
    context: ScriptContext,
): DynamicTest? {

    // Filter out sections that don't have any commands
    if (script.commands.isEmpty()) {
        return null
    }

    val title = script.getTestTitle(CodeExample)

    return dynamicTest(title, document.toUri(), TestCaseRunner(context, script))
}

fun Script.getTestTitle(commandHandler: CommandHandler): String {
    if (title != null) {
        return title!!
    }
    val command = commands.find {
        it.name == commandHandler.name
    }
    return command?.data?.textValue() ?: commandHandler.name
}
