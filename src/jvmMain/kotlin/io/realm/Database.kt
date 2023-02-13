package io.realm

import com.toxicbakery.logging.Arbor
import io.realm.model.BoulderResultRealm
import io.realm.model.ClimberRealm
import io.realm.model.LeadResultRealm
import io.realm.model.SpeedResultRealm
import scraping.model.Climber
import scraping.model.RecordType
import scraping.model.Sex
import scraping.model.boulder.BoulderGeneral
import scraping.model.lead.LeadGeneral
import scraping.model.speed.SpeedResult

/**
 * Object that allows user to perform various actions on local database.
 *
 * User may write some data to database, and later read that from it. Once saved data may also be edited, or even deleted.
 */
class Database(
    databaseName: String = "default.realm",
) {
    private val configuration = RealmConfiguration.Builder(
        schema = setOf(
            ClimberRealm::class,
            BoulderResultRealm::class,
            LeadResultRealm::class,
            SpeedResultRealm::class
        )
    )
        .name(databaseName)
        .build()
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

        Arbor.d("Writing climber with id ${climber.climberId} to database")
        this.copyToRealm(ClimberRealm().apply {
            id = climber.climberId
            sex = climber.sex?.name
            name = climber.name
            imageUrl = climber.imageUrl
            dateOfBirth = climber.dateOfBirth
            country = climber.country
            federation = climber.federation
            recordType = climber.recordType.name
        })
    }

    /**
     * Allows user to save data about all results from LEAD type of event to database.
     * In case record with given id already exists, operation will be skipped.
     *
     * @param[results] list of lead results to be saved
     * @param[date] date when event happened - used to generate unique id for the result
     * @param[competitionId] id of the competition
     */
    suspend fun writeLeadResults(
        results: List<LeadGeneral>,
        date: String,
        competitionId: String,
        competitionTitle: String,
        competitionCity: String
    ) =
        results.forEach { result ->
            writeLeadResult(result, date, competitionId, competitionTitle, competitionCity)
        }

    private suspend fun writeLeadResult(
        result: LeadGeneral,
        date: String,
        competitionId: String,
        competitionTitle: String,
        competitionCity: String
    ) {
        val resultId = generateResultId(competitionId, result.climberId)
        realm.write {
            if (!this.query<LeadResultRealm>("id == $0", resultId).find().isEmpty()) {
                Arbor.e("Lead result with id $resultId already exists - skipping")
                return@write
            }
            Arbor.d("Writing lead result with id $resultId to database")
            this.copyToRealm(LeadResultRealm().apply {
                id = resultId
                this.date = date
                this.competitionId = competitionId
                this.competitionTitle = competitionTitle
                this.competitionCity = competitionCity
                rank = result.rank
                climberId = result.climberId
                qualification = result.qualification
                semiFinal = result.semiFinal
                final = result.final
            })
        }
    }

    suspend fun writeLeadResults(
        results: List<LeadResultRealm>,
    ) =
        results.forEach { result ->
            realm.write {
                if (!this.query<LeadResultRealm>("id == $0", result.id).find().isEmpty()) {
                    Arbor.e("Lead result with id $result.id already exists - skipping")
                    return@write
                }
                Arbor.d("Writing lead result with id ${result.id} to database")
                this.copyToRealm(LeadResultRealm().apply {
                    id = result.id
                    date = result.date
                    competitionId = result.competitionId
                    competitionTitle = result.competitionTitle
                    competitionCity = result.competitionCity
                    rank = result.rank
                    climberId = result.climberId
                    qualification = result.qualification
                    semiFinal = result.semiFinal
                    final = result.final
                })
            }
        }

    suspend fun writeBoulderResults(
        results: List<BoulderGeneral>,
        date: String,
        competitionId: String,
        competitionTitle: String,
        competitionCity: String
    ) =
        results.forEach { result ->
            writeBoulderResult(result, date, competitionId, competitionTitle, competitionCity)
        }

    private suspend fun writeBoulderResult(
        result: BoulderGeneral,
        date: String,
        competitionId: String,
        competitionTitle: String,
        competitionCity: String
    ) {
        val resultId = generateResultId(competitionId, result.climberId)
        realm.write {
            if (!this.query<BoulderResultRealm>("id == $0", resultId).find().isEmpty()) {
                Arbor.e("Lead result with id $resultId already exists - skipping")
                return@write
            }
            Arbor.d("Writing boulder result with id $resultId to database")
            this.copyToRealm(BoulderResultRealm().apply {
                id = resultId
                this.date = date
                this.competitionId = competitionId
                this.competitionTitle = competitionTitle
                this.competitionCity = competitionCity
                rank = result.rank
                climberId = result.climberId
                qualification = result.qualification
                semiFinal = result.semiFinal
                final = result.final
            })
        }
    }

    suspend fun writeBoulderResults(
        results: List<BoulderResultRealm>,
    ) =
        results.forEach { result ->
            realm.write {
                if (!this.query<BoulderResultRealm>("id == $0", result.id).find().isEmpty()) {
                    Arbor.e("Lead result with id $result.id already exists - skipping")
                    return@write
                }
                Arbor.d("Writing lead result with id ${result.id} to database")
                this.copyToRealm(BoulderResultRealm().apply {
                    id = result.id
                    date = result.date
                    competitionId = result.competitionId
                    competitionTitle = result.competitionTitle
                    competitionCity = result.competitionCity
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
     * @param[date] date when event happened - used to generate unique id for the result
     * @param[competitionId] id of the competition
     */
    suspend fun writeSpeedResults(
        results: List<SpeedResult>,
        date: String,
        competitionId: String,
        competitionTitle: String,
        competitionCity: String
    ) =
        results.forEach { result ->
            writeSpeedResult(result, date, competitionId, competitionTitle, competitionCity)
        }

    suspend fun writeSpeedResult(
        result: SpeedResult,
        date: String,
        competitionId: String,
        competitionTitle: String,
        competitionCity: String
    ) {
        val resultId = generateResultId(competitionId, result.climberId)
        realm.write {
            if (!this.query<SpeedResultRealm>("id == $0", resultId).find().isEmpty()) {
                Arbor.e("Speed result with id $resultId already exists - skipping")
                return@write
            }
            Arbor.d("Writing speed result with id $resultId to database")
            this.copyToRealm(SpeedResultRealm().apply {
                id = resultId
                this.date = date
                this.competitionId = competitionId
                this.competitionTitle = competitionTitle
                this.competitionCity = competitionCity
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

    suspend fun writeSpeedResults(results: List<SpeedResultRealm>) =
        results.forEach { result ->
            realm.write {
                if (!this.query<SpeedResultRealm>("id == $0", result.id).find().isEmpty()) {
                    Arbor.e("Speed result with id $result.id already exists - skipping")
                    return@write
                }
                Arbor.d("Writing speed result with id ${result.id} to database")
                this.copyToRealm(SpeedResultRealm().apply {
                    id = result.id
                    date = result.date
                    competitionId = result.competitionId
                    competitionTitle = result.competitionTitle
                    competitionCity = result.competitionCity
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

    private fun generateResultId(competitionId: String, climberId: String) = competitionId + "_" + climberId

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
    fun getAllClimbers(): List<ClimberRealm> = realm.query<ClimberRealm>().find().sortedBy { it.id.toIntOrNull() }

    /**
     * Returns selected climber if exists.
     *
     * @param[id] id of the user to be returned
     * @return selected [climber][Climber] if exists, null otherwise
     */
    fun getClimberById(id: String): Climber? {
        val climberRealm = realm.query<ClimberRealm>("id==$0", id).first().find() ?: return null
        return Climber(
            climberId = climberRealm.id,
            name = climberRealm.name,
            imageUrl = climberRealm.imageUrl,
            sex = when (climberRealm.sex) {
                Sex.MAN.name -> Sex.MAN
                Sex.WOMAN.name -> Sex.WOMAN
                else -> null
            },
            dateOfBirth = climberRealm.dateOfBirth,
            country = climberRealm.country,
            federation = climberRealm.federation,
            recordType = when (climberRealm.recordType) {
                RecordType.OFFICIAL.name -> RecordType.OFFICIAL
                RecordType.UNOFFICIAL.name -> RecordType.UNOFFICIAL
                else -> RecordType.OFFICIAL
            }
        )
    }

    suspend fun updateClimber(id: String, newValue: Climber): Boolean {
        Arbor.d("Updating user with id $id...")
        var result = false
        realm.write {
            val climber = this.query<ClimberRealm>("id == $0", id).first().find() ?: return@write
            climber.name = newValue.name
            climber.imageUrl = newValue.imageUrl
            climber.dateOfBirth = newValue.dateOfBirth
            climber.country = newValue.country
            climber.recordType = newValue.recordType.toString()
            result = true
        }
        return result
    }

    suspend fun updateSpeedResult(id: String, newValue: SpeedResultRealm) {
        Arbor.d("Updating speed result with id $id...")
        realm.write {
            val speedResult = this.query<SpeedResultRealm>("id == $0", id).first().find() ?: return@write
            speedResult.date = newValue.date
            speedResult.competitionTitle = newValue.competitionTitle
            speedResult.competitionCity = newValue.competitionCity
            speedResult.rank = newValue.rank
            speedResult.laneA = newValue.laneA
            speedResult.laneB = newValue.laneB
            speedResult.oneEighth = newValue.oneEighth
            speedResult.quarter = newValue.quarter
            speedResult.semiFinal = newValue.semiFinal
            speedResult.smallFinal = newValue.smallFinal
            speedResult.final = newValue.final
        }
    }

    /**
     * Returns list of lead results for certain climber.
     *
     * @param[climberId] id of the user to be returned
     * @return list of [lead results][LeadResultRealm] result if any exists, empty list otherwise
     */
    fun getLeadResultsByClimberId(climberId: String): List<LeadResultRealm> =
        realm.query<LeadResultRealm>("climberId==$0", climberId).find()

    /**
     * Returns list of boulder results for certain climber.
     *
     * @param[climberId] id of the user to be returned
     * @return list of [boulder results][BoulderResultRealm] result if any exists, empty list otherwise
     */
    fun getBoulderResultsByClimberId(climberId: String): List<BoulderResultRealm> =
        realm.query<BoulderResultRealm>("climberId==$0", climberId).find()

    /**
     * Returns list of speed results for certain climber.
     *
     * @param[climberId] id of the user to be returned
     * @return list of [speed results][SpeedResultRealm] result if any exists, empty list otherwise
     */
    fun getSpeedResultsByClimberId(climberId: String): List<SpeedResultRealm> =
        realm.query<SpeedResultRealm>("climberId==$0", climberId).find()

    /**
     * Deletes climber by its id.
     */
    suspend fun deleteClimber(climberId: String) {
        Arbor.d("Deleting user with id $climberId...")
        realm.write {
            val climber: ClimberRealm = this.query<ClimberRealm>("id == $0", climberId).find().first()
            delete(climber)
        }
    }

    /**
     * Deletes lead result by its id.
     */
    suspend fun deleteLeadResult(id: String) {
        Arbor.d("Deleting lead result with id $id...")
        realm.write {
            val leadResult: LeadResultRealm = this.query<LeadResultRealm>("id == $0", id).find().first()
            delete(leadResult)
        }
    }

    /**
     * Deletes speed result by its id.
     */
    suspend fun deleteSpeedResult(id: String) {
        Arbor.d("Deleting speed result with id $id...")
        realm.write {
            val speedResult: SpeedResultRealm = this.query<SpeedResultRealm>("id == $0", id).find().first()
            delete(speedResult)
        }
    }

    /**
     * Deletes boulder result by its id.
     */
    suspend fun deleteBoulderResult(id: String) {
        Arbor.d("Deleting boulder result with id $id...")
        realm.write {
            val boulderResult: BoulderResultRealm = this.query<BoulderResultRealm>("id == $0", id).find().first()
            delete(boulderResult)
        }
    }

    fun close() {
        realm.close()
    }

    fun getSpeedResultsById(id: String): SpeedResultRealm? =
        realm.query<SpeedResultRealm>("id==$0", id).first().find()


}
