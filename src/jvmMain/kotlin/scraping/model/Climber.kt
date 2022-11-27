package scraping.model

data class Climber(
    val climberId: Int,
    val name: String,
    val age: Int?,
    val country: String,
    val federation: String
)
