import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.realm.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import scraping.Scraper

/**
 * Main Activity of the application.
 * From here user may get access to locally stored data, fetch or add new records to database, or generate comparisons
 * between climbers.
 */
@Composable
@Preview
fun App() {

    val coroutineScope = CoroutineScope(Dispatchers.IO)

    MaterialTheme {
        Column {
            Button(onClick = {
                coroutineScope.launch {
                    Scraper().fetchClimbers()
                }
            }) {
                Text("Pobierz zawodników")
            }

            Button(onClick = {
                coroutineScope.launch {
                    val climbers = Database.getAllClimbers()
                    climbers.forEach { climber ->
                        println("${climber.id} ${climber.name}")
                    }
                }
            }) {
                Text("Odczytaj zawodników")
            }

            Button(onClick = {
                coroutineScope.launch {
                    Scraper().fetchEvents()
                }
            }) {
                Text("Pobierz zawody")
            }

            Button(onClick = {
                coroutineScope.launch {
                    val leads = Database.getAllLeads()
                    leads.forEach { lead ->
                        println("${lead.id} ${lead.year} ${lead.competitionId}")
                    }
                    println(leads.size)
                }
            }) {
                Text("Odczytaj zawody LEAD")
            }

            Button(onClick = {
                coroutineScope.launch {
                    val speeds = Database.getAllSpeeds()
                    speeds.forEach { speed ->
                        println("${speed.id} ${speed.laneA}")
                    }
                    println(speeds.size)
                }
            }) {
                Text("Odczytaj zawody SPEED")
            }

            Button(onClick = {
                coroutineScope.launch {
                    val boulders = Database.getAllBoulders()
                    boulders.forEach { boulder ->
                        println("${boulder.id} ${boulder.qualification}")
                    }
                    println(boulders.size)
                }
            }) {
                Text("Odczytaj zawody BOULDER")
            }
        }
    }
}

/**
 * Generates comparison between 2 or 3 climbers.
 *
 * Displays information about climbers' ages, the greatest scores and won awards.
 */
fun compareClimbers(climber1: Int, climber2: Int, climber3: Int? = null) {

}

/**
 * Generates visualization of climber's progress over the time of one's career.
 */
fun analyzeClimberHistory(climber: Int) {

}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
