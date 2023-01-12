package utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import scraping.model.Climber
import scraping.model.RecordType
import java.io.File
import kotlin.test.BeforeTest

class FileManagerTest {

    lateinit var fileManager: FileManager

    @BeforeTest
    fun setup() {
        fileManager = FileManager()
    }

    @Test
    fun `write climber data to file`(@TempDir tempDir: File) {
        // arrange
        val climber = Climber(
            "123",
            "John Doe",
            null,
            1990,
            "USA",
            "USAC",
            RecordType.OFFICIAL
        )
        val pathName = "$tempDir/climbers.csv"

        // act
        fileManager.writeClimber(climber, pathName)

        // assert
        val writtenContent = File(pathName).readText()
        assertEquals("climberId, name, age, country, federation\n123, John Doe, 1990, USA, USAC\n", writtenContent)
    }

}
