package ui.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun HomeScreen(navigateToClimberList: () -> Unit) {

    MaterialTheme {
        Column {
            Button(onClick = {
                navigateToClimberList()
            }) {
                Text("Zawodnicy")
            }
        }
    }
}
