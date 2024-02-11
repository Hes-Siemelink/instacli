package instacli.spec

import java.nio.file.Path

object TestPaths {
    val RESOURCES: Path = Path.of("src/tests/resources")
    val TEST_CONNECTIONS: Path = RESOURCES.resolve("instacli-home/connections.yaml")
    val SAMPLE_SERVER: Path = Path.of("samples/http-server/sample-server/sample-server.cli")
    val SPEC: Path = Path.of("instacli-spec")
    val REFERENCE: Path = SPEC.resolve("reference")
}