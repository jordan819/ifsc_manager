package io.realm

import io.realm.annotations.PrimaryKey
import scraping.model.Climber
import scraping.model.lead.LeadGeneral
import scraping.model.speed.SpeedResult

class ClimberRealm : RealmObject {
    @PrimaryKey
    var id = 0
    var name: String = ""
    var yearOfBirth: Int? = null
    var country: String = ""
    var federation: String = ""
}

class LeadResultRealm : RealmObject {
    @PrimaryKey
    var id: String = ""
    var year: Int = 0
    var competitionId: String = ""
    var rank: Int? = null
    var climber: Int = 0
    var qualification: String = ""
    var semiFinal: String? = null
    var final: String? = null
}

class SpeedResultRealm : RealmObject {
    @PrimaryKey
    var id: String = ""
    var rank: Int? = null
    var climberId = 0
    var laneA: String? = null
    var laneB: String? = null
    var oneEighth: String? = null
    var quarter: String? = null
    var semiFinal: String? = null
    var smallFinal: String? = null
    var final: String? = null
}

object Database {
    private val configuration = RealmConfiguration.with(
        schema = setOf(
            ClimberRealm::class,
            LeadResultRealm::class,
            SpeedResultRealm::class
        )
    )
    private val realm = Realm.open(configuration)

    suspend fun writeClimber(climber: Climber) = realm.write {
        if (!this.query<ClimberRealm>("id == $0", climber.climberId).find().isEmpty()) {
            System.err.println("Climber with id ${climber.climberId} already exists - skipping")
            return@write
        }
        this.copyToRealm(ClimberRealm().apply {
            id = climber.climberId
            name = climber.name
            yearOfBirth = climber.yearOfBirth
            country = climber.country
            federation = climber.federation
        })
    }

    suspend fun writeLeadResults(results: List<LeadGeneral>, year: Int, competitionId: String) =
        results.forEach { result ->
            writeLeadResult(result, year, competitionId)
        }

    private suspend fun writeLeadResult(result: LeadGeneral, year: Int, competitionId: String) {
        val resultId = "$competitionId-${result.climberId}"
        realm.write {
            if (!this.query<LeadResultRealm>("id == $0", resultId).find().isEmpty()) {
                System.err.println("Lead result with id $resultId already exists - skipping")
                return@write
            }
            this.copyToRealm(LeadResultRealm().apply {
                id = resultId
                this.year = year
                this.competitionId = competitionId
                rank = result.rank
                climber = result.climberId
                qualification = result.qualification
                semiFinal = result.semiFinal
                final = result.final
            })
        }
    }

    suspend fun writeSpeedResults(results: List<SpeedResult>, year: Int, competitionId: String) =
        results.forEach { result ->
            writeSpeedResult(result, year, competitionId)
        }

    private suspend fun writeSpeedResult(result: SpeedResult, year: Int, competitionId: String) {
        val resultId = "$competitionId-${result.climberId}"
        realm.write {
            if (!this.query<SpeedResultRealm>("id == $0", resultId).find().isEmpty()) {
                System.err.println("Speed result with id $resultId already exists - skipping")
                return@write
            }
            this.copyToRealm(SpeedResultRealm().apply {
                id = resultId
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

    fun getAllLeads(): List<LeadResultRealm> = realm.query<LeadResultRealm>().find()

    fun getAllSpeeds(): List<SpeedResultRealm> = realm.query<SpeedResultRealm>().find()

    fun getAllClimbers(): List<ClimberRealm> {
        return realm.query<ClimberRealm>().find()
    }

    fun getClimberById(id: Int): ClimberRealm? = realm.query<ClimberRealm>("id==$0", id).first().find()

}
