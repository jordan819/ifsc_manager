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
import androidx.compose.material.icons.filled.Edit
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
import scraping.model.RecordType
import scraping.model.Sex
import ui.common.Dialog
import ui.common.TableCell

@Composable
fun ClimberListScreen(
    scraper: Scraper,
    database: Database,
    onBackClick: () -> Unit,
    navigateToClimberDetails: (climberId: String) -> Unit,
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

    val isEditDialogVisible = remember { mutableStateOf(false to "0") }

    fun showAddClimberDialog() {
        isAddDialogVisible.value = true
    }

    fun showEditClimberDialog(climberId: String) {
        isEditDialogVisible.value = true to climberId
    }

    fun deleteUser(climberId: String) = coroutineScope.launch {
        database.deleteClimber(climberId)
        climberList = database.getAllClimbers()
    }

    fun fetchNewClimbers() {
        coroutineScope.launch {
            scraper.fetchNewClimbers()
        }
    }

    fun sortClimberList(climbers: List<ClimberRealm>) = when (sortOption.value) {
        ClimberSortOption.ID -> climbers.sortedBy { it.id }
        ClimberSortOption.NAME -> climbers.sortedBy { it.name }
        ClimberSortOption.YEAR -> climbers.sortedBy { it.yearOfBirth }
        ClimberSortOption.COUNTRY -> climbers.sortedBy { it.country }
    }

    fun updateListDisplay() {
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

        climberList = sortClimberList(filteredClimberList)

    }

    MaterialTheme {

        if (isAddDialogVisible.value) {
            Dialog(
                title = "Dodawanie zawodnika",
                content = DialogContentAddClimber(database, coroutineScope) {
                    climberList = database.getAllClimbers()
                },
                onCloseRequest = { isAddDialogVisible.value = false },
            )
        }

        if (isEditDialogVisible.value.first) {
            Dialog(
                title = "Aktualizacja zawodnika",
                content = DialogContentEditClimber(
                    climberId = isEditDialogVisible.value.second,
                    database = database,
                    coroutineScope = coroutineScope,
                ) {
                    climberList = database.getAllClimbers()
                    isEditDialogVisible.value = false to "0"
                },
                onCloseRequest = { isEditDialogVisible.value = false to "0" },
            )
        }

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

            Spacer(Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Filtrowanie"
                    )

                    Row {
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
                                    updateListDisplay()
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
                                    updateListDisplay()
                                },
                            )
                        }
                    }
                    Row {
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
                                    updateListDisplay()
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
                                    updateListDisplay()
                                },
                            )
                        }
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sortowanie"
                    )
                    Row {
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
                                    updateListDisplay()
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
                                    updateListDisplay()
                                },
                            )
                        }
                    }
                    Row {
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
                                    updateListDisplay()
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
                                    updateListDisplay()
                                },
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(30.dp))

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
                        TableCell(text = "EDIT", weight = column6Weight)
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
                        TableCell(text = it.id, weight = column1Weight)
                        TableCell(text = it.name, weight = column2Weight)
                        TableCell(text = sex, weight = column3Weight)
                        TableCell(text = it.yearOfBirth?.toString() ?: "-", weight = column4Weight)
                        TableCell(text = it.country, weight = column5Weight)
                        TableCell(
                            image = Icons.Default.Edit,
                            weight = column6Weight,
                            onClick = { showEditClimberDialog(it.id) })
                        TableCell(
                            image = Icons.Default.Delete,
                            weight = column6Weight,
                            onClick = { deleteUser(it.id) })
                    }
                }

            }

        }
    }

}
