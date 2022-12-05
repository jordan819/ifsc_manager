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

@Composable
@Preview
fun App() {

    val coroutineScope = CoroutineScope(Dispatchers.IO)

    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Column {
            Button(onClick = {
                coroutineScope.launch {
                    Scraper().fetchClimbersWithSelenium()
                }
            }) {
                Text("Pobierz i zapisz zawodników")
            }
            Button(onClick = {
                coroutineScope.launch {
                    val climbers = Database.getAllClimbers()
                    climbers.forEach { climber ->
                        println("${climber.id} ${climber.name}")
                    }
                }
            }) {
                Text("Odczytaj zawodników z bazy")
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
