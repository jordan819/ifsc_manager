package scraping.model.speed

/**
 * Class used to fetch speed final phase result.
 */
data class SpeedFinal(
    val rank: Int?,
    val climberId: String,
    val oneEighth: String,
    val quarter: String?,
    val semiFinal: String?,
    val smallFinal: String?,
    val final: String?,
)
