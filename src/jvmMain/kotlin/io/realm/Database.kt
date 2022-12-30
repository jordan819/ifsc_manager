package io.realm

import com.toxicbakery.logging.Arbor
import io.realm.model.BoulderResultRealm
import io.realm.model.ClimberRealm
import io.realm.model.LeadResultRealm
import io.realm.model.SpeedResultRealm
import scraping.model.Climber
import scraping.model.boulder.BoulderGeneral
import scraping.model.lead.LeadGeneral
import scraping.model.speed.SpeedResult

/**
 * Object that allows user to perform various actions on local database.
 *
 * User may write some data to database, and later read that from it. Once saved data may also be edited, or even deleted.
 */
object Database {
    private val configuration = RealmConfiguration.with(
        schema = setOf(
            ClimberRealm::class,
            BoulderResultRealm::class,
            LeadResultRealm::class,
            SpeedResultRealm::class
        )
    )
    private val realm = Realm.open(configuration)

    /**
     * Allows user to save data about a certain player to database. In case record with given id already exists,
     * operation will be skipped.
     *
     * @param[climber] climber data to be saved
     */
    suspend fun writeClimber(climber: Climber) = realm.write {
        if (!this.query<ClimberRealm>("id == $0", climber.climberId).find().isEmpty()) {
            Arbor.e("Climber with id ${climber.climberId} already exists - skipping")
            return@write
        }

        this.copyToRealm(ClimberRealm().apply {
            id = climber.climberId
            sex = climber.sex?.name
            name = climber.name
            yearOfBirth = climber.yearOfBirth
            country = climber.country
            federation = climber.federation
        })
    }

    /**
     * Allows user to save data about all results from LEAD type of event to database.
     * In case record with given id already exists, operation will be skipped.
     *
     * @param[results] list of lead results to be saved
     * @param[year] year in which event happened - used to generate unique id for the result
     * @param[competitionId] id of the competition
     */
    suspend fun writeLeadResults(results: List<LeadGeneral>, year: Int, competitionId: String) =
        results.forEach { result ->
            writeLeadResult(result, year, competitionId)
        }

    private suspend fun writeLeadResult(result: LeadGeneral, year: Int, competitionId: String) {
        val resultId = generateResultId(competitionId, result.climberId)
        realm.write {
            if (!this.query<LeadResultRealm>("id == $0", resultId).find().isEmpty()) {
                Arbor.e("Lead result with id $resultId already exists - skipping")
                return@write
            }
            this.copyToRealm(LeadResultRealm().apply {
                id = resultId
                this.year = year
                this.competitionId = competitionId
                rank = result.rank
                climberId = result.climberId
                qualification = result.qualification
                semiFinal = result.semiFinal
                final = result.final
            })
        }
    }

    suspend fun writeBoulderResults(results: List<BoulderGeneral>, year: Int, competitionId: String) =
        results.forEach { result ->
            writeBoulderResult(result, year, competitionId)
        }

    private suspend fun writeBoulderResult(result: BoulderGeneral, year: Int, competitionId: String) {
        val resultId = generateResultId(competitionId, result.climberId)
        realm.write {
            if (!this.query<BoulderResultRealm>("id == $0", resultId).find().isEmpty()) {
                Arbor.e("Lead result with id $resultId already exists - skipping")
                return@write
            }
            this.copyToRealm(BoulderResultRealm().apply {
                id = resultId
                this.year = year
                this.competitionId = competitionId
                rank = result.rank
                climberId = result.climberId
                qualification = result.qualification
                semiFinal = result.semiFinal
                final = result.final
            })
        }
    }

    /**
     * Allows user to save data about all results from SPEED type of event to database.
     * In case record with given id already exists, operation will be skipped.
     *
     * @param[results] list of speed results to be saved
     * @param[year] year in which event happened - used to generate unique id for the result
     * @param[competitionId] id of the competition
     */
    suspend fun writeSpeedResults(results: List<SpeedResult>, year: Int, competitionId: String) =
        results.forEach { result ->
            writeSpeedResult(result, year, competitionId)
        }

    private suspend fun writeSpeedResult(result: SpeedResult, year: Int, competitionId: String) {
        val resultId = generateResultId(competitionId, result.climberId)
        realm.write {
            if (!this.query<SpeedResultRealm>("id == $0", resultId).find().isEmpty()) {
                Arbor.e("Speed result with id $resultId already exists - skipping")
                return@write
            }
            this.copyToRealm(SpeedResultRealm().apply {
                id = resultId
                this.year = year
                rank = result.rank
                climberId = result.climberId
                laneA = result.laneA
                laneB = result.laneB
                oneEighth = result.oneEighth
                quarter = result.quarter
                semiFinal = result.semiFinal
                smallFinal = result.smallFinal
                final = result.final
            })
        }
    }

    private fun generateResultId(competitionId: String, climberId: Int) = competitionId + "_" + climberId

    /**
     * Returns all the lead results saved in database.
     */
    fun getAllLeads(): List<LeadResultRealm> = realm.query<LeadResultRealm>().find()

    /**
     * Returns all the speed results saved in database.
     */
    fun getAllSpeeds(): List<SpeedResultRealm> = realm.query<SpeedResultRealm>().find()

    /**
     * Returns all the boulder results saved in database.
     */
    fun getAllBoulders(): List<BoulderResultRealm> = realm.query<BoulderResultRealm>().find()

    /**
     * Returns all the climbers saved in database.
     */
    fun getAllClimbers(): List<ClimberRealm> = realm.query<ClimberRealm>().find()

    /**
     * Returns selected climber if exists.
     *
     * @param[id] id of the user to be returned
     * @return selected [climber][ClimberRealm] if exists, null otherwise
     */
    fun getClimberById(id: Int): ClimberRealm? = realm.query<ClimberRealm>("id==$0", id).first().find()

    /**
     * Returns selected lead result if exists.
     *
     * @param[id] id of the user to be returned
     * @return selected [lead][LeadResultRealm] result if exists, null otherwise
     */
    fun getLeadResultById(id: Int): LeadResultRealm? = realm.query<LeadResultRealm>("id==$0", id).first().find()

    /**
     * Returns selected speed result if exists.
     *
     * @param[id] id of the user to be returned
     * @return selected [speed][SpeedResult] result if exists, null otherwise
     */
    fun getSpeedResultById(id: Int): SpeedResultRealm? = realm.query<SpeedResultRealm>("id==$0", id).first().find()

    /**
     * Deletes climber by its id.
     */
    suspend fun deleteClimber(climberId: Int) {
        Arbor.d("Deleting user with id $climberId...")
        realm.write {
            val climber: ClimberRealm = this.query<ClimberRealm>("id == $0", climberId).find().first()
            delete(climber)
        }
    }

    /**
     * Deletes lead result by its id.
     */
    fun deleteLeadResult(id: Int) {

    }

    /**
     * Deletes speed result by its id.
     */
    fun deleteSpeedResult(id: Int) {

    }

}
