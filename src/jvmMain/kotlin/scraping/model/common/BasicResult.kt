package scraping.model.common

data class BasicResult(
        val rank: Int?,
        val climberId: Int?,
        val qualification: String,
        val semiFinal: String?,
        val final: String?,
    )
