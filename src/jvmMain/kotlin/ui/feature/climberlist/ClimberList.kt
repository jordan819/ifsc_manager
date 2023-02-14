package ui.feature.climberlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import io.realm.Database
import kotlinx.coroutines.CoroutineScope
import ui.common.ErrorDisplay

class ClimberList(
    val database: Database,
    val onBackClick: () -> Unit,
    val navigateToClimberDetails: (climberId: String) -> Unit,
    val coroutineScope: CoroutineScope,
    val errorDisplay: MutableState<ErrorDisplay>,
)

@Composable
fun ClimberListUi(climberList: ClimberList) {
    ClimberListScreen(
        database = climberList.database,
        onBackClick = climberList.onBackClick,
        navigateToClimberDetails = climberList.navigateToClimberDetails,
        coroutineScope = climberList.coroutineScope,
        errorDisplay = climberList.errorDisplay
    )
}
