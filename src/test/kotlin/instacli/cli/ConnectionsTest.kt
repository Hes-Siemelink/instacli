package instacli.cli

import instacli.commands.Connections
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ConnectionsTest {

    @Test
    fun loadConnection() {
        val connections = Connections.load("instacli-home/connections.yaml")

        connections.targets shouldHaveSize 2

        val defaultServer = connections.targets["Default server"]
        defaultServer?.default shouldBe "Acme"

        val acme = defaultServer?.connections?.get("Acme")
        acme?.get("name")?.asText() shouldBe "Default connection"
    }

}