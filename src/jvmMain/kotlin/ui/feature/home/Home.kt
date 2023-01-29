package ui.feature.home

import androidx.compose.runtime.Composable
import io.realm.Database

class Home(
    val navigateToClimberList: () -> Unit,
    val database: Database,
)

@Composable
fun HomeUi(home: Home) {
    HomeScreen(
        navigateToClimberList = home.navigateToClimberList,
        database = home.database,
    )
}
