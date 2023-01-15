package scraping.model.speed

/**
 * Class used to fetch speed qualification phase result.
 */
data class SpeedQualification(
    val climberId: String,
    val laneA: String?,
    val laneB: String?,
)
