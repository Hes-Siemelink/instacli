package instacli.db

import org.junit.jupiter.api.Test
import java.sql.DriverManager

class DBTest {

    @Test
    fun testDB() {
        DriverManager.getConnection("jdbc:sqlite:").use { connection ->
            /*...*/
        }
    }
}