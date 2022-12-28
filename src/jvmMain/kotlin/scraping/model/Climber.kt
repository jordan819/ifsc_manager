package scraping.model

import kotlinx.serialization.Serializable

@Serializable
data class Climber(
    var climberId: Int = 0,
    var name: String = "",
    var sex: Sex? = null,
    var yearOfBirth: Int? = 0,
    var country: String = "",
    var federation: String = ""
)

enum class Sex {
    MAN,
    WOMAN
}
