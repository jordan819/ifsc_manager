package io.realm.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Class used to store boulder result in database.
 */
class BoulderResultRealm : RealmObject {
    @PrimaryKey
    var id: String = ""
    var date: String = ""
    var competitionId: String = ""
    var rank: Int? = null
    var climberId: String = "0"
    var qualification: String = ""
    var semiFinal: String? = null
    var final: String? = null
}
