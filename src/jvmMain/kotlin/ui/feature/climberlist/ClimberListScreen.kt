package ui.feature.climberlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.realm.Database
import io.realm.model.ClimberRealm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import scraping.Scraper
import scraping.model.RecordType
import scraping.model.Sex
import ui.common.Dialog
import ui.common.ErrorDisplay
import ui.common.TableCell
import utils.CsvHelper
import utils.FileHelper
import java.nio.file.Path

@Composable
fun ClimberListScreen(
    scraper: Scraper,
    database: Database,
    onBackClick: () -> Unit,
    navigateToClimberDetails: (climberId: String) -> Unit,
    coroutineScope: CoroutineScope,
    errorDisplay: MutableState<ErrorDisplay>,
) {

    var climberList by remember { mutableStateOf(database.getAllClimbers()) }

    // Filter List
    val hasAnyResultChecked = remember { mutableStateOf(true) }
    val isMaleChecked = remember { mutableStateOf(false) }
    val isFemaleChecked = remember { mutableStateOf(false) }
    val isOfficialChecked = remember { mutableStateOf(false) }
    val isUnofficialChecked = remember { mutableStateOf(false) }

    val sortOption = remember { mutableStateOf(ClimberSortOption.ID) }

    val isAddDialogVisible = remember { mutableStateOf(false) }
    val isEditDialogVisible = remember { mutableStateOf(false to "0") }
    val isImportDialogVisible = remember { mutableStateOf(false) }

    fun showAddClimberDialog() {
        isAddDialogVisible.value = true
    }

    fun showEditClimberDialog(climberId: String) {
        isEditDialogVisible.value = true to climberId
    }

    fun sortClimberList(climbers: List<ClimberRealm>) = when (sortOption.value) {
        ClimberSortOption.ID -> climbers.sortedBy { it.id.toIntOrNull() }
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
            database.getAllClimbers().filter { it.sex in selectedSex }
        } else {
            database.getAllClimbers()
        }

        filteredClimberList = if (selectedRecordType.isNotEmpty()) {
            filteredClimberList.filter { it.recordType in selectedRecordType }
        } else {
            filteredClimberList
        }

        filteredClimberList = if (hasAnyResultChecked.value) {
            filteredClimberList.filter { climber ->
                val leads = database.getLeadResultsByClimberId(climber.id)
                val speeds = database.getSpeedResultsByClimberId(climber.id)
                val boulders = database.getBoulderResultsByClimberId(climber.id)
                leads.isNotEmpty() || speeds.isNotEmpty() || boulders.isNotEmpty()
            }
        } else {
            filteredClimberList
        }

        climberList = sortClimberList(filteredClimberList)
    }

    fun deleteUser(climberId: String) = coroutineScope.launch {
        database.deleteClimber(climberId)
        updateListDisplay()
    }

    fun fetchNewClimbers() {
        coroutineScope.launch {
            scraper.fetchNewClimbers()
        }
    }

    updateListDisplay()
    MaterialTheme {

        if (isAddDialogVisible.value) {
            Dialog(
                title = "Dodawanie zawodnika",
                content = DialogContentAddClimber(database, coroutineScope) {
                    updateListDisplay()
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
                    updateListDisplay()
                    isEditDialogVisible.value = false to "0"
                },
                onCloseRequest = { isEditDialogVisible.value = false to "0" },
            )
        }

        if (isImportDialogVisible.value) {
            isImportDialogVisible.value = false
            val file = FileHelper().selectCsvFile()
            if (file != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val climbers = CsvHelper().readClimbers(file.path)
                        climbers.forEach {
                            database.writeClimber(it)
                        }
                    } catch (e: Exception) {
                        errorDisplay.value = ErrorDisplay(
                            message = "Wystąpił błąd podczas odczytu zawodników z pliku ${file.name}\n" +
                                    "Upewnij się, że zawarte w nim dane są poprawne.",
                            isVisible = true
                        )
                    }
                    updateListDisplay()
                }
            }
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
                },
                actions = {
                    IconButton(onClick = { showAddClimberDialog() }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.height(35.dp),
                        )
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            val climbers = database.getAllClimbers()
                            val path: Path = CsvHelper().writeClimbers(climbers)
                            withContext(Dispatchers.IO) {
                                Runtime.getRuntime()
                                    .exec("explorer.exe /select,$path")
                            }
                        }
                    }) {
                        Icon(
                            painter = painterResource("export.svg"),
                            contentDescription = null,
                            modifier = Modifier.height(35.dp),
                        )
                    }
                    IconButton(onClick = { isImportDialogVisible.value = true }) {
                        Icon(
                            painter = painterResource("import.svg"),
                            contentDescription = null,
                            modifier = Modifier.height(35.dp),
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
                                text = "Brał udział w zawodach"
                            )
                            Checkbox(
                                checked = hasAnyResultChecked.value,
                                onCheckedChange = {
                                    hasAnyResultChecked.value = it
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
                        TableCell(text = "EDIT", weight = column6Weight)
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
