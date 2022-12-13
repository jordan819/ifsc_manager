package io.realm.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

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
