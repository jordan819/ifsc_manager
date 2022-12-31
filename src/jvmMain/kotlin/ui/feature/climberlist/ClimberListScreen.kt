package ui.feature.climberlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
    navigateToClimberDetails: (climberId: Int) -> Unit,
    coroutineScope: CoroutineScope,
) {

    var climberList by remember { mutableStateOf(Database.getAllClimbers()) }

    // Filter List
    val isMaleChecked = remember { mutableStateOf(false) }
    val isFemaleChecked = remember { mutableStateOf(false) }

    val sortOption = remember { mutableStateOf(ClimberSortOption.ID) }

    fun deleteUser(climberId: Int) = coroutineScope.launch {
        database.deleteClimber(climberId)
        climberList = database.getAllClimbers()
    }

    fun fetchNewClimbers() {
        coroutineScope.launch {
            scraper.fetchNewClimbers()
        }
    }

    fun onFilterListUpdated() {
        val selectedSex = mutableListOf<String>()
        if (isMaleChecked.value) {
            selectedSex.add(Sex.MAN.name)
        }
        if (isFemaleChecked.value) {
            selectedSex.add(Sex.WOMAN.name)
        }

        climberList = if (selectedSex.isNotEmpty()) {
            Database.getAllClimbers().filter { it.sex in selectedSex }
        } else {
            Database.getAllClimbers()
        }
    }

    fun onSortOptionUpdated() {
        climberList = when (sortOption.value) {
            ClimberSortOption.ID -> Database.getAllClimbers().sortedBy { it.id }
            ClimberSortOption.NAME -> Database.getAllClimbers().sortedBy { it.name }
            ClimberSortOption.YEAR -> Database.getAllClimbers().sortedBy { it.yearOfBirth }
            ClimberSortOption.COUNTRY -> Database.getAllClimbers().sortedBy { it.country }
        }
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

            Button(onClick = {
                fetchNewClimbers()
            }) {
                Text("Pobierz nowych zawodników")
            }

            Text(
                text = "Filtrowanie"
            )
            Checkbox(
                checked = isMaleChecked.value,
                onCheckedChange = {
                    isMaleChecked.value = it
                    onFilterListUpdated()
                },
            )
            Text(
                text = "Mężczyzna"
            )

            Checkbox(
                checked = isFemaleChecked.value,
                onCheckedChange = {
                    isFemaleChecked.value = it
                    onFilterListUpdated()
                },
            )
            Text(
                text = "Kobieta"
            )

            Text(
                text = "Sortowanie"
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Id"
                )
                Checkbox(
                    checked = sortOption.value == ClimberSortOption.ID,
                    onCheckedChange = {
                        sortOption.value = ClimberSortOption.ID
                        onSortOptionUpdated()
                    },
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Imię i nazwisko"
                )
                Checkbox(
                    checked = sortOption.value == ClimberSortOption.NAME,
                    onCheckedChange = {
                        sortOption.value = ClimberSortOption.NAME
                        onSortOptionUpdated()
                    },
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rok urodzenia"
                )
                Checkbox(
                    checked = sortOption.value == ClimberSortOption.YEAR,
                    onCheckedChange = {
                        sortOption.value = ClimberSortOption.YEAR
                        onSortOptionUpdated()
                    },
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kraj"
                )
                Checkbox(
                    checked = sortOption.value == ClimberSortOption.COUNTRY,
                    onCheckedChange = {
                        sortOption.value = ClimberSortOption.COUNTRY
                        onSortOptionUpdated()
                    },
                )
            }

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
                        TableCell(text = "X", weight = column6Weight)
                    }
                }
                // Here are all the lines of your table.
                items(climberList) {
                    val sex = when (it.sex) {
                        Sex.MAN.name -> "Mężczyzna"
                        Sex.WOMAN.name -> "Kobieta"
                        else -> "-"
                    }
                    Row(Modifier.fillMaxWidth().clickable { navigateToClimberDetails(it.id) }) {
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
