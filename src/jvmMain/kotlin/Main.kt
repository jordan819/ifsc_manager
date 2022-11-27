import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import scraping.Scraper

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

fun main() {
    val scraper = Scraper()
    scraper.fetchClimbersWithSkrapeIt()
    scraper.fetchClimbersWithSelenium()
}
//    application {
//    Window(onCloseRequest = ::exitApplication) {
//        App()
//    }
//}
