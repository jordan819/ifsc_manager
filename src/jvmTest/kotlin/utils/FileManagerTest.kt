package utils

import io.realm.model.BoulderResultRealm
import io.realm.model.ClimberRealm
import io.realm.model.LeadResultRealm
import io.realm.model.SpeedResultRealm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import provider.*
import provider.BoulderResultRealmArgumentProvider
import provider.ClimberRealmArgumentProvider
import provider.LeadResultRealmArgumentProvider
import provider.SpeedResultRealmArgumentProvider
import scraping.model.RecordType
import scraping.model.Sex
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.assertTrue

class FileManagerTest {

    private lateinit var fileManager: FileManager

    @BeforeTest
    fun setup() {
        fileManager = FileManager()
    }

    @Test
    fun `create new file for climbers if not exists`(@TempDir tempDir: File) {
        // arrange
        val id = "123"
        val name = "John Doe"
        val sex = Sex.MAN
        val yearOfBirth = 1998
        val country = "USA"
        val federation = "USAC"
        val recordType = RecordType.OFFICIAL
        val climber = ClimberRealm().apply {
            this.id = id
            this.name = name
            this.sex = sex.name
            this.yearOfBirth = yearOfBirth
            this.country = country
            this.federation = federation
            this.recordType = recordType.name
        }
        val fileName = "climbers"
        val fullPath = "${tempDir.path}\\$fileName.csv"

        // act
        fileManager.writeClimber(climber, tempDir.path, fileName)

        //assert
        assertTrue(File(fullPath).exists())
    }

    @ParameterizedTest
    @ArgumentsSource(ClimberRealmArgumentProvider::class)
    fun `write climber data to file`(climber: ClimberRealm, @TempDir tempDir: File) {
        // arrange
        val fileName = "climbers"
        val fullPath = "${tempDir.path}\\$fileName.csv"

        // act
        fileManager.writeClimber(climber, tempDir.path, fileName)

        // assert
        val writtenContent = File(fullPath).readText()
        assertEquals(
            "${climber.id},${climber.name},${climber.sex},${climber.yearOfBirth},${climber.country},${climber.federation},${climber.recordType}\n",
            writtenContent
        )
    }

    @ParameterizedTest
    @ArgumentsSource(ClimberRealmArgumentProvider::class)
    fun `read climber data`(climber: ClimberRealm, @TempDir tempDir: File) {
        // arrange
        val row = with(climber) {
            "$id,$name,$sex,$yearOfBirth,$country,$federation,$recordType"
        }
        val path = "$tempDir/climbers.csv"

        File(path).writeText(row)

        // act
        val writtenContent = with(fileManager.readClimbers(path).first()) {
            "$climberId,$name,$sex,$yearOfBirth,$country,$federation,$recordType"
        }

        // assert
        assertEquals(
            row,
            writtenContent
        )
    }

    @ParameterizedTest
    @ArgumentsSource(LeadResultRealmArgumentProvider::class)
    fun `create new file for lead results if not exists`(results: List<LeadResultRealm>, @TempDir tempDir: File) {
        //arrange
        val fileName = "$tempDir/leads.csv"

        // act
        fileManager.writeLeads(results, fileName)

        // assert
        assertTrue(File(fileName).exists())
    }

    @ParameterizedTest
    @ArgumentsSource(LeadResultRealmArgumentProvider::class)
    fun `write lead results data to file`(results: List<LeadResultRealm>, @TempDir tempDir: File) {
        // arrange
        val pathName = "$tempDir/leads.csv"

        // act
        fileManager.writeLeads(results, pathName)

        // assert
        var expectedContent = ""
        results.forEach { result ->
            with(result) {
                expectedContent += "$id,$year,$competitionId,$rank,$climberId,$qualification,$semiFinal,$final\n"
            }
        }
        val writtenContent = File(pathName).readText()
        assertEquals(
            expectedContent,
            writtenContent
        )
    }

    @ParameterizedTest
    @ArgumentsSource(LeadResultRealmArgumentProvider::class)
    fun `read lead results data`(results: List<LeadResultRealm>, @TempDir tempDir: File) {
        // arrange
        var expectedContent = ""
        results.forEach { result ->
            with(result) {
                expectedContent += "$id,$year,$competitionId,$rank,$climberId,$qualification,$semiFinal,$final\n"
            }
        }
        val pathName = "$tempDir/climbers.csv"
        File(pathName).writeText(expectedContent)

        // act
        var writtenContent = ""
        fileManager.readLeads(pathName).forEach { result ->
            with(result) {
                writtenContent += "$id,$year,$competitionId,$rank,$climberId,$qualification,$semiFinal,$final\n"
            }
        }

        // assert
        assertEquals(
            expectedContent,
            writtenContent
        )
    }

    @ParameterizedTest
    @ArgumentsSource(BoulderResultRealmArgumentProvider::class)
    fun `create new file for boulder results if not exists`(results: List<BoulderResultRealm>, @TempDir tempDir: File) {
        //arrange
        val fileName = "$tempDir/leads.csv"

        // act
        fileManager.writeBoulders(results, fileName)

        // assert
        assertTrue(File(fileName).exists())
    }

    @ParameterizedTest
    @ArgumentsSource(BoulderResultRealmArgumentProvider::class)
    fun `write boulder results data to file`(results: List<BoulderResultRealm>, @TempDir tempDir: File) {
        // arrange
        val pathName = "$tempDir/boulders.csv"

        // act
        fileManager.writeBoulders(results, pathName)

        // assert
        var expectedContent = ""
        results.forEach { result ->
            with(result) {
                expectedContent += "$id,$year,$competitionId,$rank,$climberId,$qualification,$semiFinal,$final\n"
            }
        }
        val writtenContent = File(pathName).readText()
        assertEquals(
            expectedContent,
            writtenContent
        )
    }

    @ParameterizedTest
    @ArgumentsSource(BoulderResultRealmArgumentProvider::class)
    fun `read boulder results data`(results: List<BoulderResultRealm>, @TempDir tempDir: File) {
        // arrange
        var expectedContent = ""
        results.forEach { result ->
            with(result) {
                expectedContent += "$id,$year,$competitionId,$rank,$climberId,$qualification,$semiFinal,$final\n"
            }
        }
        val pathName = "$tempDir/boulder.csv"
        File(pathName).writeText(expectedContent)

        // act
        var writtenContent = ""
        fileManager.readBoulders(pathName).forEach { result ->
            with(result) {
                writtenContent += "$id,$year,$competitionId,$rank,$climberId,$qualification,$semiFinal,$final\n"
            }
        }

        // assert
        assertEquals(
            expectedContent,
            writtenContent
        )
    }

    @ParameterizedTest
    @ArgumentsSource(SpeedResultRealmArgumentProvider::class)
    fun `create new file for speed results if not exists`(results: List<SpeedResultRealm>, @TempDir tempDir: File) {
        //arrange
        val fileName = "$tempDir/speeds.csv"

        // act
        fileManager.writeSpeeds(results, fileName)

        // assert
        assertTrue(File(fileName).exists())
    }

    @ParameterizedTest
    @ArgumentsSource(SpeedResultRealmArgumentProvider::class)
    fun `write speed results data to file`(results: List<SpeedResultRealm>, @TempDir tempDir: File) {
        // arrange
        val pathName = "$tempDir/speeds.csv"

        // act
        fileManager.writeSpeeds(results, pathName)

        // assert
        var expectedContent = ""
        results.forEach { result ->
            with(result) {
                expectedContent += "$id,$year,$rank,$climberId,$laneA,$laneB,$oneEighth,$quarter,$semiFinal,$smallFinal,$final\n"
            }
        }
        val writtenContent = File(pathName).readText()
        assertEquals(
            expectedContent,
            writtenContent
        )
    }

    @ParameterizedTest
    @ArgumentsSource(SpeedResultRealmArgumentProvider::class)
    fun `read speed results data`(results: List<SpeedResultRealm>, @TempDir tempDir: File) {
        // arrange
        var expectedContent = ""
        results.forEach { result ->
            with(result) {
                expectedContent += "$id,$year,$rank,$climberId,$laneA,$laneB,$oneEighth,$quarter,$semiFinal,$smallFinal,$final\n"
            }
        }
        val pathName = "$tempDir/speeds.csv"
        File(pathName).writeText(expectedContent)

        // act
        var writtenContent = ""
        fileManager.readSpeeds(pathName).forEach { result ->
            with(result) {
                writtenContent += "$id,$year,$rank,$climberId,$laneA,$laneB,$oneEighth,$quarter,$semiFinal,$smallFinal,$final\n"
            }
        }

        // assert
        assertEquals(
            expectedContent,
            writtenContent
        )
    }

}
