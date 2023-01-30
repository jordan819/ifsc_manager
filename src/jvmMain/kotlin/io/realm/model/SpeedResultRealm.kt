package io.realm.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Class used to store speed result in database.
 */
class SpeedResultRealm : RealmObject {
    @PrimaryKey
    var id: String = ""
    var date: String = ""
    var competitionId: String = ""
    var rank: Int? = null
    var climberId: String = "0"
    var laneA: String? = null
    var laneB: String? = null
    var oneEighth: String? = null
    var quarter: String? = null
    var semiFinal: String? = null
    var smallFinal: String? = null
    var final: String? = null
}
