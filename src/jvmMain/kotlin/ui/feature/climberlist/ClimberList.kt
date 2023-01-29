package ui.feature.climberlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import io.realm.Database
import kotlinx.coroutines.CoroutineScope
import scraping.Scraper
import ui.common.ErrorDisplay

class ClimberList(
    val scraper: Scraper,
    val database: Database,
    val onBackClick: () -> Unit,
    val navigateToClimberDetails: (climberId: String) -> Unit,
    val coroutineScope: CoroutineScope,
    val errorDisplay: MutableState<ErrorDisplay>,
)

@Composable
fun ClimberListUi(climberList: ClimberList) {
    ClimberListScreen(
        scraper = climberList.scraper,
        database = climberList.database,
        onBackClick = climberList.onBackClick,
        navigateToClimberDetails = climberList.navigateToClimberDetails,
        coroutineScope = climberList.coroutineScope,
        errorDisplay = climberList.errorDisplay
    )
}
