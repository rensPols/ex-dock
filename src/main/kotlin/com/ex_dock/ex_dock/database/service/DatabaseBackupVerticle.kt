package com.ex_dock.ex_dock.database.service

import com.ex_dock.ex_dock.database.connection.Connection
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.EventBus
import io.vertx.sqlclient.Pool
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * Verticle for all events connected to database backups
 */
class DatabaseBackupVerticle: AbstractVerticle() {
  private lateinit var client: Pool
  private lateinit var eventBus: EventBus
  private lateinit var frequency: String
  private lateinit var time: String
  private lateinit var dbUser: String
  private lateinit var dbPassword: String
  private var savedBackups = 1

  /**
   * Initialize variables from properties and start the eventbus and scheduler
   */
  override fun start() {
    client = Connection().getConnection(vertx)
    eventBus = vertx.eventBus()

    //Read the variables from the properties file
    try {
      val props: Properties = javaClass.classLoader.getResourceAsStream("secret.properties").use {
        Properties().apply { load(it) }
      }

      frequency = props.getProperty("BACKUP_FREQUENCY")
      time = props.getProperty("BACKUP_TIME")
      savedBackups = props.getProperty("SAVED_BACKUPS").toInt()
      dbUser = props.getProperty("DATABASE_USERNAME")
      dbPassword = props.getProperty("DATABASE_PASSWORD")

    } catch (e: Exception) {
      println(e.message)
      try {
        val isDocker: Boolean = !System.getenv("GITHUB_RUN_NUMBER").isNullOrEmpty()
        if (isDocker) {
          frequency = "daily"
          time = "00:00"
          savedBackups = 1
        } else {
          error("Could not load the Properties file!")
        }
      } catch (e: Exception) {
        println(e.message)
        error("Could not read the Properties file!")
      }
    }

    backupWithEventBusRequest()
    scheduler()
  }

  /**
   * Makes a full backup of the current state of the database
   */
  private fun backupDatabaseData() {
    organizeBackups()
    val zdt: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault())
    val zdtTruncatedHour: ZonedDateTime = zdt.truncatedTo(ChronoUnit.HOURS)
    val f: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH", Locale.getDefault())
    val timeString = zdtTruncatedHour.format(f)
      .replace(" ", "_")
      .replace("/", "_") +
      ".sql"

    val backupDirectory = System.getProperty("user.dir") + "\\database\\backup\\backup_$timeString"
    val processBuilder = ProcessBuilder(
      "pg_dump",
      "--host", "localhost",
      "--port", "8890",
      "--file", backupDirectory,
      "--blobs",
      "--verbose",
      "--username", dbUser,
      "--no-password",
      "ex-dock"
    )

    try {
      val env = processBuilder.environment()
      env["PGPASSWORD"] = dbPassword
      val p: Process = processBuilder.start()
      val bufferedReader = BufferedReader(InputStreamReader(p.errorStream))
      var line = bufferedReader.readLine()
      while (line!= null) {
        println(line)
        line = bufferedReader.readLine()
      }
    } catch (e: IOException) {
      e.printStackTrace()
    }
  }

  /**
   * Gets the request for a backup from the eventbus
   */
  private fun backupWithEventBusRequest() {
    eventBus.consumer<String>("process.service.backup").handler { message ->
      backupDatabaseData()
      message.reply("Completed backup!")
    }
  }

  /**
   * Creates a scheduler for making backups at specified intervals
   */
  private fun scheduler() {
    val frequencyInt: Long = getFrequencyTimeNumber(frequency)
    val timeInt: Long = getTimeNumber(time)
    val currentTime: Long = LocalTime.now(ZoneId.systemDefault())[ChronoField.MILLI_OF_DAY].toLong()
    var waitingTime: Long = timeInt - currentTime

    if (waitingTime < 0) {
      val tempTimeInt = getTimeNumber("23:59")
      waitingTime = (tempTimeInt - currentTime) + timeInt
    }

    vertx.setTimer(waitingTime) {
      backupDatabaseData()

      vertx.setPeriodic(frequencyInt*1000L) {
        backupDatabaseData()
      }
    }
  }

  /**
   * Converts the given frequency to a usable number for the scheduler
   */
  private fun getFrequencyTimeNumber(frequency: String): Long {
    return when (frequency) {
      "daily" -> 1*24*60*60
      "weekly" -> 7*24*60*60
      "monthly" -> 30*24*60*60
      "hourly" -> 60*60
      else -> 1
    }
  }

  /**
   * Gets the time in milliseconds for the wanted backup time
   */
  private fun getTimeNumber(time: String): Long {
    val parts = time.split(":")
    return (parts[0].toInt() * 60 * 60 + parts[1].toInt() * 60) * 1000L
  }

  /**
   * Removes the oldest backup if the number of backups is greater than the maximum number of backups allowed
   */
  private fun organizeBackups() {
    val backupDirectory = System.getProperty("user.dir") + "\\database\\backup"
    val folder = File(backupDirectory)
    val listOfFiles = folder.listFiles()
    val fileDateList: MutableList<String> = emptyList<String>().toMutableList()
    var oldestDate = ""

    if (listOfFiles!!.size >= savedBackups) {
      for (f in listOfFiles) {
        val fileDateString = f.name
          .replace("backup_", "")
          .replace(".sql", "")
        fileDateList.add(fileDateString)
      }

      for (dateString in fileDateList) {
        oldestDate = if (oldestDate != "") {
          val dateParts = dateString.split("_")
          val oldestDateParts = oldestDate.split("_")

          checkIfOlderFile(
                dateParts,
                oldestDateParts,
                dateString,
                oldestDate)
        } else {
          dateString
        }
      }

      val oldestBackup = File("$backupDirectory\\backup_$oldestDate.sql")

      if (oldestBackup.delete()) {
        println("Deleted oldest backup from: $oldestDate")
      } else {
        println("Could not delete oldest backup from: $oldestDate")
      }
    }
  }

  /**
   * Checks the backup name and returns the oldest backup date
   */
  private fun checkIfOlderFile(dateParts: List<String>,
                               oldestDateParts: List<String>,
                               dateString: String,
                               oldestDate: String): String {
    if (dateParts[2].toInt() < oldestDateParts[2].toInt()) {
      return dateString
    } else if (dateParts[2].toInt() == oldestDateParts[2].toInt()) {
      if (dateParts[1].toInt() < oldestDateParts[1].toInt()) {
        return dateString
      } else if (dateParts[1].toInt() == oldestDateParts[1].toInt()) {
        if (dateParts[0].toInt() < oldestDateParts[0].toInt()) {
          return dateString
        } else if (dateParts[3].toInt() < oldestDateParts[3].toInt()) {
          return dateString
        }
      }
    }

    return oldestDate
  }
}
