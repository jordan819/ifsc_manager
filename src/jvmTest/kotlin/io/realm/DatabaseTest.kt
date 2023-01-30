package io.realm

import io.realm.model.BoulderResultRealm
import io.realm.model.LeadResultRealm
import io.realm.model.SpeedResultRealm
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import provider.*
import scraping.model.Climber
import scraping.model.RecordType
import scraping.model.Sex
import scraping.model.boulder.BoulderGeneral
import scraping.model.lead.LeadGeneral
import scraping.model.speed.SpeedResult
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class DatabaseTest {

    private lateinit var database: Database

    @BeforeEach
    fun setUp() {
        database = Database("test.realm")
    }

    @AfterEach
    fun tearDown() {
        database.close()
        File("test.realm.management").delete()
        File("test.realm").delete()
        File("test.realm.lock").delete()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @ParameterizedTest
    @ArgumentsSource(ClimberArgumentProvider::class)
    fun `write and read single climber`(climber: Climber) = runTest {
        // act
        database.writeClimber(climber)

        //assert
        val savedClimber = database.getClimberById(climber.climberId)
        assertEquals(climber, savedClimber)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @ParameterizedTest
    @ArgumentsSource(ClimberListArgumentProvider::class)
    fun `write and read multiple climbers`(climbers: List<Climber>) = runTest {
        // act
        climbers.forEach { climber ->
            database.writeClimber(climber)
        }

        //assert
        climbers.forEach { climber ->
            val savedClimber = database.getClimberById(climber.climberId)
            assertEquals(climber, savedClimber)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @ParameterizedTest
    @ArgumentsSource(LeadGeneralArgumentProvider::class)
    fun `write and read lead results`(results: List<LeadGeneral>) = runTest {
        //arrange
        val date = "2020-06-12"
        val competitionId = "1385_12"

        //act
        database.writeLeadResults(results, date, competitionId)

        //assert
        val savedResults = database.getAllLeads()
        savedResults.forEachIndexed { index, savedResult ->
            val expectedResult = LeadResultRealm().apply {
                this.id = competitionId + "_" + results[index].climberId
                this.date = date
                this.competitionId = competitionId
                this.rank = results[index].rank
                this.climberId = results[index].climberId
                this.qualification = results[index].qualification
                this.semiFinal = results[index].semiFinal
                this.final = results[index].final
            }
            assertAll(
                { assertEquals(expectedResult.id, savedResult.id) },
                { assertEquals(expectedResult.date, savedResult.date) },
                { assertEquals(expectedResult.competitionId, savedResult.competitionId) },
                { assertEquals(expectedResult.rank, savedResult.rank) },
                { assertEquals(expectedResult.climberId, savedResult.climberId) },
                { assertEquals(expectedResult.qualification, savedResult.qualification) },
                { assertEquals(expectedResult.semiFinal, savedResult.semiFinal) },
                { assertEquals(expectedResult.final, savedResult.final) },
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @ParameterizedTest
    @ArgumentsSource(BoulderGeneralArgumentProvider::class)
    fun `write and read boulder results`(results: List<BoulderGeneral>) = runTest {
        //arrange
        val date = "2020-06-12"
        val competitionId = "1385_12"

        //act
        database.writeBoulderResults(results, date, competitionId)

        //assert
        val savedResults = database.getAllBoulders()
        savedResults.forEachIndexed { index, savedResult ->
            val expectedResult = BoulderResultRealm().apply {
                this.id = competitionId + "_" + results[index].climberId
                this.date = date
                this.competitionId = competitionId
                this.rank = results[index].rank
                this.climberId = results[index].climberId
                this.qualification = results[index].qualification
                this.semiFinal = results[index].semiFinal
                this.final = results[index].final
            }
            assertAll(
                { assertEquals(expectedResult.id, savedResult.id) },
                { assertEquals(expectedResult.date, savedResult.date) },
                { assertEquals(expectedResult.competitionId, savedResult.competitionId) },
                { assertEquals(expectedResult.rank, savedResult.rank) },
                { assertEquals(expectedResult.climberId, savedResult.climberId) },
                { assertEquals(expectedResult.qualification, savedResult.qualification) },
                { assertEquals(expectedResult.semiFinal, savedResult.semiFinal) },
                { assertEquals(expectedResult.final, savedResult.final) },
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @ParameterizedTest
    @ArgumentsSource(SpeedResultArgumentProvider::class)
    fun `write and read speed results`(results: List<SpeedResult>) = runTest {
        //arrange
        val date = "2020-06-12"
        val competitionId = "1385_12"

        //act
        database.writeSpeedResults(results, date, competitionId)

        //assert
        val savedResults = database.getAllSpeeds()
        savedResults.forEachIndexed { index, savedResult ->
            val expectedResult = SpeedResultRealm().apply {
                this.id = competitionId + "_" + results[index].climberId
                this.date = date
                this.rank = results[index].rank
                this.climberId = results[index].climberId
                this.laneA = results[index].laneA
                this.laneB = results[index].laneB
                this.oneEighth = results[index].oneEighth
                this.quarter = results[index].quarter
                this.semiFinal = results[index].semiFinal
                this.smallFinal = results[index].smallFinal
                this.final = results[index].final
            }
            assertAll(
                { assertEquals(expectedResult.id, savedResult.id) },
                { assertEquals(expectedResult.date, savedResult.date) },
                { assertEquals(expectedResult.rank, savedResult.rank) },
                { assertEquals(expectedResult.climberId, savedResult.climberId) },
                { assertEquals(expectedResult.laneA, savedResult.laneA) },
                { assertEquals(expectedResult.laneB, savedResult.laneB) },
                { assertEquals(expectedResult.oneEighth, savedResult.oneEighth) },
                { assertEquals(expectedResult.quarter, savedResult.quarter) },
                { assertEquals(expectedResult.smallFinal, savedResult.smallFinal) },
                { assertEquals(expectedResult.semiFinal, savedResult.semiFinal) },
                { assertEquals(expectedResult.final, savedResult.final) },
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `update climber data`() = runTest {
        //arrange
        val climberId = "1"
        val climber = Climber(
            climberId,
            "John",
            Sex.MAN,
            null,
            "USA",
            "USAC",
            RecordType.OFFICIAL,
        )
        val newClimber = Climber(
            climberId,
            "Alex Murphy",
            Sex.MAN,
            1998,
            "USA",
            "USAC",
            RecordType.UNOFFICIAL,
        )
        database.writeClimber(climber)

        //act
        database.updateClimber(climberId, newClimber)

        //assert
        val savedClimber = database.getClimberById(climberId)
        assertEquals(newClimber, savedClimber)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `delete the only climber`() = runTest {
        //arrange
        val climberId = "1"
        val climber = Climber(
            climberId,
            "John",
            Sex.MAN,
            null,
            "USA",
            "USAC",
            RecordType.OFFICIAL,
        )
        database.writeClimber(climber)

        //act
        database.deleteClimber(climberId)

        //assert
        assertTrue(database.getAllClimbers().isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `delete one climber from many`() = runTest {
        //arrange
        val climberId1 = "1"
        val climberId2 = "2"
        val climberId3 = "3"
        val climber1 = Climber(
            climberId1,
            "John",
            Sex.MAN,
            null,
            "USA",
            "USAC",
            RecordType.OFFICIAL,
        )
        val climber2 = Climber(
            climberId2,
            "Eleonora",
            Sex.WOMAN,
            2000,
            "USA",
            "USAC",
            RecordType.OFFICIAL,
        )
        val climber3 = Climber(
            climberId3,
            "John",
            Sex.MAN,
            null,
            "USA",
            "USAC",
            RecordType.OFFICIAL,
        )
        database.writeClimber(climber1)
        database.writeClimber(climber2)
        database.writeClimber(climber3)

        //act
        database.deleteClimber(climberId1)

        //assert
        assertAll(
            { assertFalse(database.getAllClimbers().isEmpty()) },
            { assertFalse(climberId1 in database.getAllClimbers().map { it.id }) },
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `delete the only lead result`() = runTest {
        //arrange
        val leadResult = LeadGeneral(
            1,
            "123",
            "12",
            "11",
            null,
        )
        database.writeLeadResults(listOf(leadResult), "2020-06-12", "123_S")
        val resultId = database.getAllLeads().first().id

        //act
        database.deleteLeadResult(resultId)

        //assert
        assertTrue(database.getAllLeads().isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `delete one lead result from many`() = runTest {
        //arrange
        val leadResults = listOf(
            LeadGeneral(
                1,
                "1",
                "12",
                "11",
                null,
            ),
            LeadGeneral(
                2,
                "2",
                "12",
                "11",
                null,
            ),
            LeadGeneral(
                3,
                "3",
                "12",
                "11",
                null,
            )
        )
        database.writeLeadResults(leadResults, "2020-06-12", "MS-1")
        val resultId = database.getAllLeads()[1].id

        //act
        database.deleteLeadResult(resultId)

        //assert
        assertAll(
            { assertFalse(database.getAllLeads().isEmpty()) },
            { assertFalse(resultId in database.getAllLeads().map { it.id }) },
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `delete the only boulder result`() = runTest {
        //arrange
        val boulderResult = BoulderGeneral(
            1,
            "123",
            "12",
            "11",
            null,
        )
        database.writeBoulderResults(listOf(boulderResult), "2020-06-12", "123_S")
        val resultId = database.getAllBoulders().first().id

        //act
        database.deleteBoulderResult(resultId)

        //assert
        assertTrue(database.getAllBoulders().isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `delete one boulder result from many`() = runTest {
        //arrange
        val boulderResults = listOf(
            BoulderGeneral(
                1,
                "1",
                "12",
                "11",
                null,
            ),
            BoulderGeneral(
                2,
                "2",
                "12",
                "11",
                null,
            ),
            BoulderGeneral(
                3,
                "3",
                "12",
                "11",
                null,
            )
        )
        database.writeBoulderResults(boulderResults, "2020-06-12", "MS-1")
        val resultId = database.getAllBoulders()[1].id

        //act
        database.deleteBoulderResult(resultId)

        //assert
        assertAll(
            { assertFalse(database.getAllBoulders().isEmpty()) },
            { assertFalse(resultId in database.getAllBoulders().map { it.id }) },
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `delete the only speed result`() = runTest {
        //arrange
        val speedResult = SpeedResult(
            1,
            "123",
            "12",
            "11",
            "11.1",
            "13",
            "10",
            "13",
            null,
        )
        database.writeSpeedResults(listOf(speedResult), "2020-06-12", "123_S")
        val resultId = database.getAllSpeeds().first().id

        //act
        database.deleteSpeedResult(resultId)

        //assert
        assertTrue(database.getAllSpeeds().isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `delete one speed result from many`() = runTest {
        //arrange
        val speedResults = listOf(
            SpeedResult(
                1,
                "1",
                "12",
                "11",
                "11.1",
                "13",
                "10",
                "13",
                null,
            ),
            SpeedResult(
                1,
                "2",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
            ),
            SpeedResult(
                1,
                "3",
                "12",
                "11",
                "11.1",
                "13",
                "10",
                "13",
                "10.1",
            )
        )
        database.writeSpeedResults(speedResults, "2020-06-12", "MS-1")
        val resultId = database.getAllSpeeds()[1].id

        //act
        database.deleteSpeedResult(resultId)

        //assert
        assertAll(
            { assertFalse(database.getAllSpeeds().isEmpty()) },
            { assertFalse(resultId in database.getAllSpeeds().map { it.id }) },
        )
    }

}
