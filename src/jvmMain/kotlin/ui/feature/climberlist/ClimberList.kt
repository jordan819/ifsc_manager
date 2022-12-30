package ui.feature.climberlist

import androidx.compose.runtime.Composable
import io.realm.Database
import scraping.Scraper

class ClimberList(
    val scraper: Scraper,
    val database: Database,
    val onBackClick: () -> Unit,
) {
}

@Composable
fun ClimberListUi(climberList: ClimberList) {
    ClimberListScreen(
        scraper = climberList.scraper,
        database = climberList.database,
        onBackClick = climberList.onBackClick,
    )
}
