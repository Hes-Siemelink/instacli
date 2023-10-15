package instacli.spec

import instacli.loadTestCases
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.TestFactory

class DataManipulationTests {

    @TestFactory
    fun `Cli scripts`(): List<DynamicNode> {
        return loadTestCases(
            "data-manipulation/For each and merge.cli",
            "data-manipulation/Merge data.cli",
            "data-manipulation/Replace handler tests.cli",
            "data-manipulation/Object tests.yaml",
//            "data-manipulation/Join handler tests.cli",
        )
    }
}

