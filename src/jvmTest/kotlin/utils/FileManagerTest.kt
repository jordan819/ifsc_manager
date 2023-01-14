package utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import scraping.model.Climber
import scraping.model.RecordType
import scraping.model.Sex
import java.io.File
import java.util.stream.Stream
import kotlin.test.BeforeTest
import kotlin.test.assertTrue

class FileManagerTest {

    private lateinit var fileManager: FileManager

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

    @ParameterizedTest
    @ArgumentsSource(ClimberArgumentProvider::class)
    fun `write climber data to file`(climber: Climber, @TempDir tempDir: File) {
        // arrange
        val pathName = "$tempDir/climbers.csv"

        // act
        fileManager.writeClimber(climber, pathName)

        // assert
        val writtenContent = File(pathName).readText()
        assertEquals(
            "${climber.climberId},${climber.name},${climber.sex},${climber.yearOfBirth},${climber.country},${climber.federation},${climber.recordType}\n",
            writtenContent
        )
    }

    @ParameterizedTest
    @ArgumentsSource(ClimberArgumentProvider::class)
    fun `read climber data`(climber: Climber, @TempDir tempDir: File) {
        // arrange
        val row = with(climber) {
            "$climberId,$name,$sex,$yearOfBirth,$country,$federation,$recordType"
        }
        val pathName = "$tempDir/climbers.csv"

        // act
        File(pathName).writeText(row)

        // assert
        val writtenContent = with(fileManager.readClimbers(pathName).first()) {
            "$climberId,$name,$sex,$yearOfBirth,$country,$federation,$recordType"
        }
        assertEquals(
            row,
            writtenContent
        )
    }

    internal class ClimberArgumentProvider : ArgumentsProvider {
        @Throws(Exception::class)
        override fun provideArguments(context: ExtensionContext): Stream<out Arguments?> {
            return Stream.of(
                Arguments.of(
                    Climber(
                        "123",
                        "John Doe",
                        null,
                        null,
                        "USA",
                        "USAC",
                        RecordType.UNOFFICIAL
                    )
                ),
                Arguments.of(
                    Climber(
                        "321",
                        "Arkadiusz Justyński",
                        Sex.MAN,
                        1987,
                        "POL",
                        "Polska Federacja Wspinaczki Sportowej",
                        RecordType.UNOFFICIAL
                    )
                ),
                Arguments.of(
                    Climber(
                        "420",
                        "Fanny Gibert",
                        Sex.WOMAN,
                        1998,
                        "FRA",
                        "Federation Française de la Montagne et de l'Escalade",
                        RecordType.OFFICIAL
                    )
                ),
            )
        }
    }

}
