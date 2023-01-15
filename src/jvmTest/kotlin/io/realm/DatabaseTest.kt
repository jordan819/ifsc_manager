package io.realm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import provider.ClimberArgumentProvider
import scraping.model.Climber
import java.io.File
import kotlin.test.assertEquals

internal class DatabaseTest {

    private lateinit var database: Database
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

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

    @ParameterizedTest
    @ArgumentsSource(ClimberArgumentProvider::class)
    fun `write and read single climber to database`(climber: Climber) {
        // act
        coroutineScope.launch {
            database.writeClimber(climber)
        }

        //assert
        coroutineScope.launch {
            val savedClimber = database.getClimberById(climber.climberId)
            assertEquals(climber, savedClimber)
        }
    }

    @Test
    fun writeLeadResults() {
    }

    @Test
    fun writeBoulderResults() {
    }

    @Test
    fun writeSpeedResults() {
    }

    @Test
    fun getAllLeads() {
    }

    @Test
    fun getAllSpeeds() {
    }

    @Test
    fun getAllBoulders() {
    }

    @Test
    fun getAllClimbers() {
    }

    @Test
    fun getClimberById() {
    }

    @Test
    fun updateClimber() {
    }

    @Test
    fun getLeadResultsByClimberId() {
    }

    @Test
    fun getBoulderResultsByClimberId() {
    }

    @Test
    fun getSpeedResultsByClimberId() {
    }

    @Test
    fun deleteClimber() {
    }

    @Test
    fun deleteLeadResult() {
    }

    @Test
    fun deleteSpeedResult() {
    }
}
