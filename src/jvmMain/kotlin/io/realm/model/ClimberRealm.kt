package io.realm.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Class used to store climber in database.
 */
class ClimberRealm : RealmObject {
    @PrimaryKey
    var id: String = "0"
    var name: String = ""
    var sex: String? = null
    var dateOfBirth: String? = null
    var country: String = ""
    var federation: String = ""
    var recordType: String = ""
}
