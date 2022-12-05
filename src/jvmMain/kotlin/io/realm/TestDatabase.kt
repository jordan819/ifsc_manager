package io.realm

import io.realm.annotations.PrimaryKey
import scraping.model.Climber
import java.lang.IllegalArgumentException

class ClimberRealm : RealmObject {
    @PrimaryKey
    var id = 0
    var name: String = ""
    var age: Int? = null
    var country: String = ""
    var federation: String = ""
}

object Database {
    private val configuration = RealmConfiguration.with(schema = setOf(ClimberRealm::class))
    private val realm = Realm.open(configuration)

    suspend fun write(climber: Climber) = realm.write {
        try {
            this.copyToRealm(ClimberRealm().apply {
                id = climber.climberId
                name = climber.name
                age = climber.age
                country = climber.country
                federation = climber.federation
            })
        } catch (_: IllegalArgumentException) {
            println("Climber with id ${climber.climberId} already exists - skipping")
        }
    }

    fun getAllClimbers(): List<ClimberRealm> {
        return realm.query<ClimberRealm>().find()
    }

}
