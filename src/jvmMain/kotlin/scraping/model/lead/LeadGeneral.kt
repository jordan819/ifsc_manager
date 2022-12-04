package scraping.model.lead

/**
 * The simplest type of results - all data is available in general table.
 */
data class LeadGeneral(
    val rank: Int?,
    val climberId: Int,
    val qualification: String,
    val semiFinal: String?,
    val final: String?
)
