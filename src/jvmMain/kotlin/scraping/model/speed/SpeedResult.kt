package scraping.model.speed

data class SpeedResult(
    val rank: Int?,
    val climberId: Int,
    val laneA: String?,
    val laneB: String?,
    val oneEighth: String?,
    val quarter: String?,
    val semiFinal: String?,
    val smallFinal: String?,
    val final: String?,
)
