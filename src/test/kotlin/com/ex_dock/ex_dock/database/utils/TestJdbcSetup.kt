package com.ex_dock.ex_dock.database.utils

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.io.ByteArrayInputStream
import java.util.Properties
import org.mockito.Mockito.*

class JdbcSetupTest {

  private lateinit var jdbcSetup: JdbcSetup
  private lateinit var mockPropsStream: ByteArrayInputStream
  private lateinit var mockDefaultPropsStream: ByteArrayInputStream

  @BeforeEach
  fun setUp() {
    // Mock the contents of the properties files
    val secretPropertiesContent = """
            db.username=secretUser
            db.password=secretPass
        """.trimIndent()
    val defaultPropertiesContent = """
            db.username=defaultUser
            db.password=defaultPass
            db.url=jdbc:mysql://localhost:3306/mydb
        """.trimIndent()

    // Mock property files streams
    mockPropsStream = ByteArrayInputStream(secretPropertiesContent.toByteArray())
    mockDefaultPropsStream = ByteArrayInputStream(defaultPropertiesContent.toByteArray())

    // Create a mock of the class loader's resource stream methods
    val mockClassLoader = mock(ClassLoader::class.java)
    `when`(mockClassLoader.getResourceAsStream("secret.properties")).thenReturn(mockPropsStream)
    `when`(mockClassLoader.getResourceAsStream("default.properties")).thenReturn(mockDefaultPropsStream)
    jdbcSetup = JdbcSetup("secret.properties", "default.properties", mockClassLoader)

//    `when`(javaClass.classLoader).thenReturn(mockClassLoader)

  }

  @Test
  fun `test setup with default properties`() {
    // Call setup with default parameters
    jdbcSetup.setup()

    // Check that properties were loaded from the default file
    val props = jdbcSetup.getProperties()
    assertEquals("defaultUser", props["db.username"])
    assertEquals("defaultPass", props["db.password"])
    assertEquals("jdbc:mysql://localhost:3306/mydb", props["db.url"])
  }

  @Test
  fun `test setup with input map overrides`() {
    val inputMap = mapOf<Any, Any>(
      "db.username" to "customUser",
      "db.password" to "customPass"
    )

    // Call setup with the inputMap provided
    jdbcSetup.setup(inputMap)

    // Check that the properties were correctly set from inputMap and defaults
    val props = jdbcSetup.getProperties()
    assertEquals("customUser", props["db.username"])
    assertEquals("customPass", props["db.password"])
    assertEquals("jdbc:mysql://localhost:3306/mydb", props["db.url"])
  }

  @Test
  fun `test setup throws exception if already completed`() {
    // Call setup once
    jdbcSetup.setup()

    // Expect IllegalStateException if we try to run setup again without reset
    val exception = assertThrows<IllegalStateException> {
      jdbcSetup.setup()
    }

    assertEquals("Setup has already been completed", exception.message)
  }

  @Test
  fun `test setup reset works`() {
    // Call setup once
    jdbcSetup.setup()

    // Call setup again with reset flag set to true
    jdbcSetup.setup(reset = true)

    // Ensure properties were reloaded and isCompleted is still true
    val props = jdbcSetup.getProperties()
    assertEquals("defaultUser", props["db.username"])
    assertEquals("defaultPass", props["db.password"])
    assertTrue(jdbcSetup.isCompleted())
  }

  // Utility function to get access to private properties (for testing purposes)
  private fun JdbcSetup.getProperties(): Properties {
    val propsField = JdbcSetup::class.java.getDeclaredField("props")
    propsField.isAccessible = true
    return propsField.get(this) as Properties
  }

  private fun JdbcSetup.isCompleted(): Boolean {
    val isCompletedField = JdbcSetup::class.java.getDeclaredField("isCompleted")
    isCompletedField.isAccessible = true
    return isCompletedField.get(this) as Boolean
  }
}
