package ui.feature.climberlist

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import io.realm.Database
import scraping.Scraper

@Composable
fun ClimberListScreen(
    scraper: Scraper,
    database: Database,
    onBackClick: () -> Unit
) {

    MaterialTheme {
        Column {
            TopAppBar(
                title = {
                    Text(
                        text = "Zawodnicy"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )

            Text(text = "Lista zawodników")

            Button(onClick = {
                val climbers = database.getAllClimbers()
                climbers.forEach { climber ->
                    println(climber)
                }
            }) {
                Text("Wyświetl zawodników")
            }

        }
    }

}
