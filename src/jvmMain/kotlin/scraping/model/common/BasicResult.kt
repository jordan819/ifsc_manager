package scraping.model.common

data class BasicResult(
    val rank: Int?,
    val climberId: String?,
    val qualification: String,
    val semiFinal: String?,
    val final: String?,
)
