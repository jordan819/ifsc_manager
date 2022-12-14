package ui.feature.climberdetails

import androidx.compose.runtime.Composable
import io.realm.Database
import kotlinx.coroutines.CoroutineScope

class ClimberDetails(
    val climberId: String,
    val database: Database,
    val onBackClick: () -> Unit,
    val coroutineScope: CoroutineScope,
)

@Composable
fun ClimberDetailsUi(climberDetails: ClimberDetails) {
    ClimberDetailsScreen(
        climberId = climberDetails.climberId,
        database = climberDetails.database,
        onBackClick = climberDetails.onBackClick,
        coroutineScope = climberDetails.coroutineScope,
    )
}
