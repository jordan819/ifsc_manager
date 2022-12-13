package io.realm.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

class ClimberRealm : RealmObject {
    @PrimaryKey
    var id = 0
    var name: String = ""
    var yearOfBirth: Int? = null
    var country: String = ""
    var federation: String = ""
}
