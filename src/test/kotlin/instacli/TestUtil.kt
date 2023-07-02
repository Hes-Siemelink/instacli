package instacli

import instacli.script.files.runCliScriptFile
import java.io.File
import java.io.FileNotFoundException

fun test(resource: String) {
    println("Running tests for ${resource}")

    runCliScriptFile(toFile(resource))

    println()
}

fun toFile(resource: String): File {
    val testDir = File("instacli-spec/test")
    val testFile = File(testDir, resource)
    if (!testFile.exists()) {
        throw FileNotFoundException(testFile.absolutePath)
    }

    return testFile
}

