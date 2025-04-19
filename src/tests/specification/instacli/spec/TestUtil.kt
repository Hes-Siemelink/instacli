package instacli.spec

import com.fasterxml.jackson.databind.node.TextNode
import instacli.cli.reportError
import instacli.commands.connections.Credentials
import instacli.commands.connections.CredentialsFile
import instacli.commands.connections.setCredentials
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
import kotlin.io.path.writeText

//
// All
//

fun Path.getTests(): List<DynamicNode> {
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


//
// Instacli tests
//

/**
 * Gets all individual test cases in a script file as a dynamic tests.
 */
fun CliFile.getTestCases(): List<DynamicTest> {
    val context = CliFileContext(file)

    context.setCredentials(tempCredentials())

    return script.splitTestCases().map { script ->
        dynamicTest(script.getTitle(TestCase), file.toUri(), TestCaseRunner(context, script))
    }
}

private fun tempCredentials(): CredentialsFile {
    val credentials = Credentials.fromFile(TestPaths.TEST_CREDENTIALS)

    val tempFile = Files.createTempFile("instacli-connections-", ".yaml")
    tempFile.toFile().deleteOnExit()
    credentials.file = tempFile

    return credentials
}

private fun tempCredentials(testDir: Path, localCredentials: Boolean): CredentialsFile {
    return if (localCredentials) {
        Credentials.fromFile(testDir.resolve(Credentials.FILENAME))
    } else {
        CredentialsFile()
    }
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

fun Script.getTitle(commandHandler: CommandHandler): String {
    if (title != null) {
        return title!!
    }
    val command = commands.find {
        it.name == commandHandler.name
    }
    return command?.data?.textValue() ?: commandHandler.name
}

//
// Code examples in Markdown files
//

fun CliFile.getCodeExamples(): List<DynamicTest> {

    // Set up test dir with helper files from document
    val testDir = Files.createTempDirectory("instacli-")
    val context = CliFileContext(testDir)
    context.variables[SCRIPT_TEMP_DIR_VARIABLE] =
        TextNode(testDir.toAbsolutePath().toString()) // XXX encapsulate TEMP_DIR in ScriptContext

    val helperFiles = markdown?.helperFiles ?: emptyMap()

    helperFiles.forEach {
        println("Helper file: ${it.key}")
        val targetFile = testDir.resolve(it.key)
        Files.createDirectories(targetFile.parent)
        targetFile.writeText(it.value)
    }
    val credentials = tempCredentials(testDir, helperFiles.containsKey(Credentials.FILENAME))

    val scripts = splitMarkdown()
    val instacliTests: List<DynamicTest> = scripts
        .mapNotNull {
            toTestFromScript(file, it, context, credentials)
        }

    return instacliTests
}

private fun toTestFromScript(
    document: Path,
    script: Script,
    context: ScriptContext,
    credentials: CredentialsFile
): DynamicTest? {

    // Filter out sections that don't have any commands
    if (script.commands.isEmpty()) {
        return null
    }

    context.setCredentials(credentials)
    UserPrompt.default = TestPrompt

    val title = script.getTitle(CodeExample)

    return dynamicTest(title, document.toUri(), TestCaseRunner(context, script))
}