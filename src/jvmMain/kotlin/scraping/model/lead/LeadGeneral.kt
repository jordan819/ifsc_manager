package scraping.model.lead

/**
 * Class used to fetch lead general result.
 */
data class LeadGeneral(
    val rank: Int?,
    val climberId: Int,
    val qualification: String,
    val semiFinal: String?,
    val final: String?
)
