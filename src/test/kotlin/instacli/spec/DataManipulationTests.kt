package instacli.spec

import instacli.test
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class DataManipulationTests {

    @Test
    fun forEachAndMerge() {
        test("data-manipulation/For each and merge.cli")
    }

    @Test
    fun merge() {
        test("data-manipulation/Merge data.cli")
    }

    @Test
    @Disabled
    fun join() {
        test("data-manipulation/Join handler tests.cli")
    }

    @Test
    fun replace() {
        test("data-manipulation/Replace handler tests.cli")
    }

    @Test
    fun objectCreation() {
        test("data-manipulation/Object tests.yaml")
    }
}

