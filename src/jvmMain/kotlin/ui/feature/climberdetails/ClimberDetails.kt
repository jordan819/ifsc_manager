package ui.feature.climberdetails

import androidx.compose.runtime.Composable
import io.realm.Database
import kotlinx.coroutines.CoroutineScope

class ClimberDetails(
    val climberId: String,
    val database: Database,
    val onBackClick: () -> Unit,
    val coroutineScope: CoroutineScope,
) {
    val climber = database.getClimberById(climberId)
}

@Composable
fun ClimberDetailsUi(climberDetails: ClimberDetails) {
    ClimberDetailsScreen(
        climber = climberDetails.climber,
        onBackClick = climberDetails.onBackClick,
        database = climberDetails.database,
        coroutineScope = climberDetails.coroutineScope,
    )
}
