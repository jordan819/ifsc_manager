package io.realm

import io.realm.annotations.PrimaryKey
import scraping.model.Climber
import scraping.model.lead.LeadGeneral

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

object Database {
    private val configuration = RealmConfiguration.with(schema = setOf(ClimberRealm::class, LeadResultRealm::class))
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

    fun getAllLeads(): List<LeadResultRealm> = realm.query<LeadResultRealm>().find()

    fun getAllClimbers(): List<ClimberRealm> {
        return realm.query<ClimberRealm>().find()
    }

    fun getClimberById(id: Int): ClimberRealm? = realm.query<ClimberRealm>("id==$0", id).first().find()

}
