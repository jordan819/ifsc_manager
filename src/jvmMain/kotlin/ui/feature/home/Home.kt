package ui.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import io.realm.Database
import kotlinx.coroutines.CoroutineScope
import ui.common.ErrorDisplay

class Home(
    val navigateToClimberList: () -> Unit,
    val database: Database,
    val errorDisplay: MutableState<ErrorDisplay>,
    val coroutineScope: CoroutineScope,
)

@Composable
fun HomeUi(home: Home) {
    HomeScreen(
        navigateToClimberList = home.navigateToClimberList,
        database = home.database,
        errorDisplay = home.errorDisplay,
        coroutineScope = home.coroutineScope,
    )
}
