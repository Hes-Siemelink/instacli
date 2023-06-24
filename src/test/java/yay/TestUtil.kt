package yay

import yay.core.YayScript
import java.io.File
import java.io.FileNotFoundException

fun test(resource: String) {
    println("Running tests for ${resource}")

    YayScript.run(toFile(resource))

    println()
}

fun toFile(resource: String): File {
    val testDir = File("yay-spec/test")
    val testFile = File(testDir, resource)
    if (!testFile.exists()) {
        throw FileNotFoundException(testFile.absolutePath)
    }

    return testFile
}

