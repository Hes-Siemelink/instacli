package instacli.cli

import instacli.commands.Connections
import instacli.spec.TestPaths
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ConnectionsTest {

    @Test
    fun loadConnection() {
        val connections = Connections.load(TestPaths.TEST_CONNECTIONS)

        connections.targets shouldHaveSize 2

        val defaultServer = connections.targets["Default server"]
        checkNotNull(defaultServer) { "Connections should contain 'Default server'" }
        defaultServer.default shouldBe "Default account"

        val defaultAccount = defaultServer.defaultAccount()
        checkNotNull(defaultAccount) { "Default server should have a default account" }
        defaultAccount.get("name").asText() shouldBe "Default account"
        defaultAccount.get("description").asText() shouldBe "The one and only"
    }
}