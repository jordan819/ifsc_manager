package ui.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.realm.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import scraping.Scraper

@Composable
fun HomeScreen(navigateToClimberList: () -> Unit) {

    MaterialTheme {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Button(onClick = {
                navigateToClimberList()
            }) {
                Text("Zawodnicy")
            }
            // TODO: probably remove the following buttons
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    Scraper(Database()).fetchEvents()
                }
            }) {
                Text("Pobierz eventy")
            }
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    Scraper(Database()).fetchAllClimbers()
                }
            }) {
                Text("Pobierz zawodników")
            }
        }
    }
}
