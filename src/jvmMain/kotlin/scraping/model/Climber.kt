package scraping.model

import kotlinx.serialization.Serializable

@Serializable
data class Climber(
    var climberId: String,
    var name: String = "",
    var sex: Sex? = null,
    var yearOfBirth: Int? = 0,
    var country: String = "",
    var federation: String = "",
    var recordType: RecordType
)

enum class Sex {
    MAN,
    WOMAN
}

enum class RecordType {
    OFFICIAL,
    UNOFFICIAL
}
