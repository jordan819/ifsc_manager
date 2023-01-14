package scraping.model.lead

/**
 * Class used to fetch lead general result.
 */
data class LeadGeneral(
    val rank: Int?,
    val climberId: String,
    val qualification: String,
    val semiFinal: String?,
    val final: String?
)
