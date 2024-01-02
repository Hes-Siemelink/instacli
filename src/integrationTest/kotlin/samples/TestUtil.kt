package samples

import java.io.FileNotFoundException
import java.nio.file.Path
import kotlin.io.path.exists

fun toPath(resource: String): Path {
    val testDir = Path.of("samples")
    val testFile = testDir.resolve(resource)
    if (!testFile.exists()) {
        throw FileNotFoundException(testFile.toAbsolutePath().toString())
    }

    return testFile
}

