package scraping.model.speed

/**
 * Class used to gather all speed result in one place. Combines [SpeedQualification] and [SpeedFinal]
 */
data class SpeedResult(
    val rank: Int?,
    val climberId: String,
    val laneA: String?,
    val laneB: String?,
    val oneEighth: String?,
    val quarter: String?,
    val semiFinal: String?,
    val smallFinal: String?,
    val final: String?,
)
