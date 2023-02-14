package scraping.model

import kotlinx.serialization.Serializable

@Serializable
data class Climber(
    var climberId: String,
    var name: String = "",
    var imageUrl: String,
    var sex: Sex? = null,
    var dateOfBirth: String?,
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
