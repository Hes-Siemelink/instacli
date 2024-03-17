package instacli

import java.nio.file.Path

object TestPaths {
    val RESOURCES: Path = Path.of("src/tests/resources")
    val TEST_CREDENTIALS: Path = RESOURCES.resolve("instacli-home/credentials.yaml")
}