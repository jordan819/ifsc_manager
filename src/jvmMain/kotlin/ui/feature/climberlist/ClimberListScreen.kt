package ui.feature.climberlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.realm.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import scraping.Scraper
import scraping.model.RecordType
import scraping.model.Sex
import ui.common.TableCell
import ui.common.TableCellImage

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
    val isOfficialChecked = remember { mutableStateOf(false) }
    val isUnofficialChecked = remember { mutableStateOf(false) }

    val sortOption = remember { mutableStateOf(ClimberSortOption.ID) }

    val isAddDialogVisible = remember { mutableStateOf(false) }

    fun showAddClimberDialog() {
        isAddDialogVisible.value = true
    }

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

        val selectedRecordType = mutableListOf<String>()
        if (isOfficialChecked.value) {
            selectedRecordType.add(RecordType.OFFICIAL.name)
        }
        if (isUnofficialChecked.value) {
            selectedRecordType.add(RecordType.UNOFFICIAL.name)
        }

        var filteredClimberList = if (selectedSex.isNotEmpty()) {
            Database.getAllClimbers().filter { it.sex in selectedSex }
        } else {
            Database.getAllClimbers()
        }

        filteredClimberList = if (selectedRecordType.isNotEmpty()) {
            filteredClimberList.filter { it.recordType in selectedRecordType }
        } else {
            filteredClimberList
        }

        climberList = filteredClimberList

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

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = {
                    fetchNewClimbers()
                }) {
                    Text("Pobierz nowych zawodników")
                }
                Button(onClick = {
                    showAddClimberDialog()
                }) {
                    Text("Dodaj zawodnika")
                }
            }

            if (isAddDialogVisible.value) {
                Dialog(
                    title = "Dodawanie zawodnika",
                    content = DialogContentAddClimber(database, coroutineScope),
                    onCloseRequest = { isAddDialogVisible.value = false },
                )
            }


            Text(
                text = "Filtrowanie"
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Oficjalny"
                )
                Checkbox(
                    checked = isOfficialChecked.value,
                    onCheckedChange = {
                        isOfficialChecked.value = it
                        onFilterListUpdated()
                    },
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nieoficjalny"
                )
                Checkbox(
                    checked = isUnofficialChecked.value,
                    onCheckedChange = {
                        isUnofficialChecked.value = it
                        onFilterListUpdated()
                    },
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mężczyzna"
                )
                Checkbox(
                    checked = isMaleChecked.value,
                    onCheckedChange = {
                        isMaleChecked.value = it
                        onFilterListUpdated()
                    },
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kobieta"
                )
                Checkbox(
                    checked = isFemaleChecked.value,
                    onCheckedChange = {
                        isFemaleChecked.value = it
                        onFilterListUpdated()
                    },
                )
            }

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
                        TableCellImage(
                            image = Icons.Default.Delete,
                            weight = column6Weight,
                            onClick = { deleteUser(it.id) })

                    }
                }

            }

        }
    }

}
