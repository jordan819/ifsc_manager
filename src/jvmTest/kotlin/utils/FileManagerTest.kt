package utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import scraping.model.Climber
import scraping.model.RecordType
import scraping.model.Sex
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.assertTrue

class FileManagerTest {

    lateinit var fileManager: FileManager

    @BeforeTest
    fun setup() {
        fileManager = FileManager()
    }

    @Test
    fun `create new file if not exists`(@TempDir tempDir: File) {
        // arrange
        val id = "123"
        val name = "John Doe"
        val sex = Sex.MAN
        val yearOfBirth = 1998
        val country = "USA"
        val federation = "USAC"
        val recordType = RecordType.OFFICIAL
        val climber = Climber(
            id,
            name,
            sex,
            yearOfBirth,
            country,
            federation,
            recordType
        )
        val pathName = "$tempDir/climbers.csv"

        // act
        fileManager.writeClimber(climber, pathName)

        //assert
        assertTrue(File(pathName).exists())
    }

    @Test
    fun `write climber data to file`(@TempDir tempDir: File) {
        // arrange
        val id = "123"
        val name = "John Doe"
        val sex = Sex.MAN
        val yearOfBirth = 1998
        val country = "USA"
        val federation = "USAC"
        val recordType = RecordType.OFFICIAL
        val climber = Climber(
            id,
            name,
            sex,
            yearOfBirth,
            country,
            federation,
            recordType
        )
        val pathName = "$tempDir/climbers.csv"

        // act
        fileManager.writeClimber(climber, pathName)

        // assert
        val writtenContent = File(pathName).readText()
        assertEquals(
            "climberId, name, age, country, federation\n$id, $name, $yearOfBirth, $country, $federation\n",
            writtenContent
        )
    }

    @Test
    fun `write climber data with nullable fields set to null to file`(@TempDir tempDir: File) {
        // arrange
        val id = "123"
        val name = "John Doe"
        val sex = null
        val yearOfBirth = null
        val country = "USA"
        val federation = "USAC"
        val recordType = RecordType.UNOFFICIAL
        val climber = Climber(
            id,
            name,
            sex,
            yearOfBirth,
            country,
            federation,
            recordType
        )
        val pathName = "$tempDir/climbers.csv"

        // act
        fileManager.writeClimber(climber, pathName)

        // assert
        val writtenContent = File(pathName).readText()
        assertEquals(
            "climberId, name, age, country, federation\n$id, $name, $yearOfBirth, $country, $federation\n",
            writtenContent
        )
    }

}
