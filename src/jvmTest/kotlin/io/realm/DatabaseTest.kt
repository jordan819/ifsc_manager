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

internal class DatabaseTest {

    private lateinit var database: Database

    @BeforeEach
    fun setUp() {
        database = Database("test.realm")
    }

    @AfterEach
    internal fun tearDown() {
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
        val year = 2020
        val competitionId = "1385_12"

        //act
        database.writeLeadResults(results, year, competitionId)

        //assert
        val savedResults = database.getAllLeads()
        savedResults.forEachIndexed { index, savedResult ->
            val expectedResult = LeadResultRealm().apply {
                this.id = competitionId + "_" + results[index].climberId
                this.year = year
                this.competitionId = competitionId
                this.rank = results[index].rank
                this.climberId = results[index].climberId
                this.qualification = results[index].qualification
                this.semiFinal = results[index].semiFinal
                this.final = results[index].final
            }
            assertAll(
                { assertEquals(expectedResult.id, savedResult.id) },
                { assertEquals(expectedResult.year, savedResult.year) },
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
        val year = 2020
        val competitionId = "1385_12"

        //act
        database.writeBoulderResults(results, year, competitionId)

        //assert
        val savedResults = database.getAllBoulders()
        savedResults.forEachIndexed { index, savedResult ->
            val expectedResult = BoulderResultRealm().apply {
                this.id = competitionId + "_" + results[index].climberId
                this.year = year
                this.competitionId = competitionId
                this.rank = results[index].rank
                this.climberId = results[index].climberId
                this.qualification = results[index].qualification
                this.semiFinal = results[index].semiFinal
                this.final = results[index].final
            }
            assertAll(
                { assertEquals(expectedResult.id, savedResult.id) },
                { assertEquals(expectedResult.year, savedResult.year) },
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
        val year = 2020
        val competitionId = "1385_12"

        //act
        database.writeSpeedResults(results, year, competitionId)

        //assert
        val savedResults = database.getAllSpeeds()
        savedResults.forEachIndexed { index, savedResult ->
            val expectedResult = SpeedResultRealm().apply {
                this.id = competitionId + "_" + results[index].climberId
                this.year = year
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
                { assertEquals(expectedResult.year, savedResult.year) },
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

}
