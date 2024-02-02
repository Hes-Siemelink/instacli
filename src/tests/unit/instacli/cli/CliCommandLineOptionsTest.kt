package instacli.cli

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CliCommandLineOptionsTest {

    @Test
    fun noFlag() {
        instacli.cli.noFlag("-home") shouldBe "home"
        instacli.cli.noFlag("--home") shouldBe "home"
        instacli.cli.noFlag("---home") shouldBe "home"
        instacli.cli.noFlag("---home-dir") shouldBe "home-dir"
        instacli.cli.noFlag("home-dir") shouldBe "home-dir"
    }

    @Test
    fun isFlag() {
        instacli.cli.isFlag("-h") shouldBe true
        instacli.cli.isFlag("--home") shouldBe true
        instacli.cli.isFlag("home") shouldBe false
        instacli.cli.isFlag("home-dir") shouldBe false
    }

    @Test
    fun toParameterMap() {
        instacli.cli.toParameterMap(
            listOf(
                "--home",
                "~",
                "--target",
                "/tmp"
            )
        ) shouldBe
                mapOf(
                    "home" to "~",
                    "target" to "/tmp"
                )
    }
}