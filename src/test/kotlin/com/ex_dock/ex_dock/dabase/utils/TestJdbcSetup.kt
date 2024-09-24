import com.ex_dock.ex_dock.database.utils.JdbcSetup
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows


import io.mockk.*
import java.util.Properties

class TestJdbcSetup {
  private lateinit var jdbcSetup: JdbcSetup

  @BeforeEach
  fun setUp() {
    // Mocking the class loader to provide fake .properties files
    mockkStatic(this::class.java.classLoader)

    val secretProps = Properties().apply {
      setProperty("db.username", "user1")
      setProperty("db.password", "password1")
    }

    val defaultProps = Properties().apply {
      setProperty("db.username", "defaultUser")
      setProperty("db.password", "defaultPassword")
    }

    every { this::class.java.classLoader.getResourceAsStream("test_secret.properties") } returns secretProps.toInputStream()
    every { this::class.java.classLoader.getResourceAsStream("test_default.properties") } returns defaultProps.toInputStream()

    jdbcSetup = JdbcSetup("test_secret.properties", "test_default.properties")
  }

  @Test
  fun `test default properties are loaded`() {
    jdbcSetup.setup()

    // Here we will test if the properties were set from the defaults or secret props
    assert(jdbcSetup.getProperty("db.username") == "defaultUser")
    assert(jdbcSetup.getProperty("db.password") == "defaultPassword")
  }

  @Test
  fun `test input map overrides default properties`() {
    val inputMap = mapOf(
      "db.username" to "overrideUser",
      "db.password" to "overridePassword"
    )

    jdbcSetup.setup(inputMap)

    // Verifying that the input map overrides the properties
    assert(jdbcSetup.getProperty("db.username") == "overrideUser")
    assert(jdbcSetup.getProperty("db.password") == "overridePassword")
  }

  @Test
  fun `test setup does not run twice unless reset`() {
    jdbcSetup.setup()

    assertThrows<IllegalStateException> {
      jdbcSetup.setup()
    }

    // Test reset
    jdbcSetup.setup(reset = true)
    assertDoesNotThrow {
      jdbcSetup.setup(reset = true)
    }
  }
}
