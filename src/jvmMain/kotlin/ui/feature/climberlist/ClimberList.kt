package ui.feature.climberlist

import androidx.compose.runtime.Composable
import io.realm.Database
import kotlinx.coroutines.CoroutineScope
import scraping.Scraper

class ClimberList(
    val scraper: Scraper,
    val database: Database,
    val onBackClick: () -> Unit,
    val coroutineScope: CoroutineScope,
) {
    val climbers = database.getAllClimbers()
}

@Composable
fun ClimberListUi(climberList: ClimberList) {
    ClimberListScreen(
        scraper = climberList.scraper,
        database = climberList.database,
        onBackClick = climberList.onBackClick,
        coroutineScope = climberList.coroutineScope,
        climbers = climberList.climbers
    )
}
