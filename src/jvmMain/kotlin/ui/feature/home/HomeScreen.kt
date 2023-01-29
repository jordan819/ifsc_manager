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
import androidx.compose.ui.awt.ComposeWindow
import com.toxicbakery.logging.Arbor
import io.realm.Database
import io.realm.internal.platform.OS_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import scraping.Scraper
import utils.FileManager
import java.awt.FileDialog
import java.io.File
import java.nio.file.Path

@Composable
fun HomeScreen(
    navigateToClimberList: () -> Unit,
    database: Database,
) {

    val isAddDialogVisible = remember { mutableStateOf(false) }

    fun selectCsvFile(
        window: ComposeWindow? = null,
        title: String = "",
        allowedExtensions: List<String> = listOf("csv")
    ): File? {
        return FileDialog(window, title, FileDialog.LOAD).apply {
            isMultipleMode = false

            if (OS_NAME.contains("Windows")) {
                file = allowedExtensions.joinToString(";") { "*$it" }
            } else if (OS_NAME.contains("Linux")) {
                setFilenameFilter { _, name ->
                    allowedExtensions.any {
                        name.endsWith(it)
                    }
                }
            } else {
                // TODO: add support for MacOS
                Arbor.wtf("Unknown OS")
            }

            isVisible = true
        }.files.firstOrNull()
    }

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
                    var path = Path.of("")
                    climbers.forEach { climber ->
                        path = FileManager().writeClimber(climber)
                    }
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
                val file = selectCsvFile()
                if (file != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val climbers = FileManager().readClimbers(file.path)
                        climbers.forEach {
                            database.writeClimber(it)
                        }
                    }
                }
            }

        }
    }
}
