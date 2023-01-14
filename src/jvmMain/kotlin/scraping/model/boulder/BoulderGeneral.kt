package scraping.model.boulder

data class BoulderGeneral(
    val rank: Int?,
    val climberId: String,
    val qualification: String,
    val semiFinal: String?,
    val final: String?
)
