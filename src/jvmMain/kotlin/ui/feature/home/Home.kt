package ui.feature.home

import androidx.compose.runtime.Composable

class Home(
    val navigateToClimberList: () -> Unit
) {
}

@Composable
fun HomeUi(home: Home) {
    HomeScreen(
        navigateToClimberList = home.navigateToClimberList
    )
}
