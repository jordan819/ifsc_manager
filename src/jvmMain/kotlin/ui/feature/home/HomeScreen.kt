package ui.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.realm.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import scraping.Scraper
import utils.CsvHelper
import utils.FileHelper
import java.nio.file.Path

@Composable
fun HomeScreen(
    navigateToClimberList: () -> Unit,
    database: Database,
) {

    val isAddDialogVisible = remember { mutableStateOf(false) }

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
                    Scraper(database).fetchEvents()
                }
            }) {
                Text("Pobierz eventy")
            }
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    Scraper(database).fetchAllClimbers()
                }
            }) {
                Text("Pobierz zawodników")
            }
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val climbers = database.getAllClimbers()
                    val path: Path = CsvHelper().writeClimbers(climbers)
                    withContext(Dispatchers.IO) {
                        Runtime.getRuntime()
                            .exec("explorer.exe /select,$path")
                    }
                }
            }) {
                Text("Eksportuj zawodników")
            }
            Button(onClick = {
                isAddDialogVisible.value = true
            }) {
                Text("Importuj zawodników")
            }

            if (isAddDialogVisible.value) {
                isAddDialogVisible.value = false
                val file = FileHelper().selectCsvFile()
                if (file != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val climbers = CsvHelper().readClimbers(file.path)
                        climbers.forEach {
                            database.writeClimber(it)
                        }
                    }
                }
            }

        }
    }
}
