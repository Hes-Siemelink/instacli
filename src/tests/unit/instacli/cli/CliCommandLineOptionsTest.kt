package instacli.cli

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CliCommandLineOptionsTest {

    @Test
    fun noFlag() {
        noFlag("-home") shouldBe "home"
        noFlag("--home") shouldBe "home"
        noFlag("---home") shouldBe "home"
        noFlag("---home-dir") shouldBe "home-dir"
        noFlag("home-dir") shouldBe "home-dir"
    }

    @Test
    fun isFlag() {
        isFlag("-h") shouldBe true
        isFlag("--home") shouldBe true
        isFlag("home") shouldBe false
        isFlag("home-dir") shouldBe false
    }

    @Test
    fun toParameterMap() {
        toParameterMap(
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