package instacli.cli

import instacli.TestPaths
import instacli.commands.connections.Credentials
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CredentialsTest {

    @Test
    fun loadConnection() {
        val credentials = Credentials.load(TestPaths.TEST_CREDENTIALS)

        credentials.targetResources shouldHaveSize 2

        val defaultServer = credentials.targetResources["Default server"]
        checkNotNull(defaultServer) { "Connections should contain 'Default server'" }
        defaultServer.default shouldBe "Default account"

        val defaultAccount = defaultServer.default()
        checkNotNull(defaultAccount) { "Default server should have a default account" }
        defaultAccount.get("name").asText() shouldBe "Default account"
        defaultAccount.get("description").asText() shouldBe "The one and only"
    }
}