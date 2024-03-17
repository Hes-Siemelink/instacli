package instacli.spec

import java.nio.file.Path

object TestPaths {
    val README: Path = Path.of("README.md")
    val RESOURCES: Path = Path.of("src/tests/resources")
    val TEST_CREDENTIALS: Path = RESOURCES.resolve("instacli-home/credentials.yaml")
    val SAMPLE_SERVER: Path = Path.of("samples/http-server/sample-server/sample-server.cli")
    val SPEC: Path = Path.of("instacli-spec")
    val COMMANDS: Path = SPEC.resolve("commands")
    val LANGUAGE: Path = SPEC.resolve("language")
}