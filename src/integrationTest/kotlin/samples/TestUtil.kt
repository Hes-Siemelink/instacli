package samples

import java.io.File
import java.io.FileNotFoundException

fun toFile(resource: String): File {
    val testDir = File("samples")
    val testFile = File(testDir, resource)
    if (!testFile.exists()) {
        throw FileNotFoundException(testFile.absolutePath)
    }

    return testFile
}

