package ui.feature.climberlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.realm.Database
import io.realm.model.ClimberRealm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import scraping.Scraper
import scraping.model.Sex
import ui.common.TableCell

@Composable
fun ClimberListScreen(
    scraper: Scraper,
    database: Database,
    onBackClick: () -> Unit,
    coroutineScope: CoroutineScope,
    climbers: List<ClimberRealm>,
) {

    var climberList by remember { mutableStateOf(climbers) }

    fun deleteUser(climberId: Int) = coroutineScope.launch {
        database.deleteClimber(climberId)
        climberList = database.getAllClimbers()
    }

    MaterialTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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

            // Each cell of a column must have the same weight.
            val column1Weight = .1f // 10%
            val column2Weight = .4f // 40%
            val column3Weight = .1f // 10%
            val column4Weight = .2f // 20%
            val column5Weight = .15f // 15%
            val column6Weight = .05f // 5%
            // The LazyColumn will be our table. Notice the use of the weights below
            LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
                // Here is the header
                item {
                    Row(Modifier.background(Color.Gray)) {
                        TableCell(text = "Id", weight = column1Weight)
                        TableCell(text = "Imię i nazwisko", weight = column2Weight)
                        TableCell(text = "Płeć", weight = column3Weight)
                        TableCell(text = "Rok urodzenia", weight = column4Weight)
                        TableCell(text = "Kraj", weight = column5Weight)
                        TableCell(text = "Usuwanie", weight = column6Weight)
                    }
                }
                // Here are all the lines of your table.
                items(climberList) {
                    val sex = when (it.sex) {
                        Sex.MAN.name -> "Mężczyzna"
                        Sex.WOMAN.name -> "Kobieta"
                        else -> "-"
                    }
                    Row(Modifier.fillMaxWidth()) {
                        TableCell(text = it.id.toString(), weight = column1Weight)
                        TableCell(text = it.name, weight = column2Weight)
                        TableCell(text = sex, weight = column3Weight)
                        TableCell(text = it.yearOfBirth?.toString() ?: "-", weight = column4Weight)
                        TableCell(text = it.country, weight = column5Weight)
                        TableCell(text = "DEL", weight = column6Weight, onClick = { deleteUser(it.id) })

                    }
                }

            }

        }
    }

}
