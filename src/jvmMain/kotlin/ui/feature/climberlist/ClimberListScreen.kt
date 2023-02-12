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
import scraping.model.RecordType
import scraping.model.Sex
import ui.common.Dialog
import ui.common.ErrorDisplay
import ui.common.TableCell
import utils.AppColors
import utils.CsvHelper
import utils.FileHelper
import java.nio.file.Path

@Composable
fun ClimberListScreen(
    database: Database,
    onBackClick: () -> Unit,
    navigateToClimberDetails: (climberId: String) -> Unit,
    coroutineScope: CoroutineScope,
    errorDisplay: MutableState<ErrorDisplay>,
) {

    val initDone = remember { mutableStateOf(false) }

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
    val importMode = remember { mutableStateOf<InputDataType?>(null) }

    val isImportDropdownExpanded = remember { mutableStateOf(false) }
    val isSortDropdownExpanded = remember { mutableStateOf(false) }
    val isFilterDropdownExpanded = remember { mutableStateOf(false) }

    val isDeletingEnabled = remember { mutableStateOf(false) }

    fun showAddClimberDialog() {
        isAddDialogVisible.value = true
    }

    fun showEditClimberDialog(climberId: String) {
        isEditDialogVisible.value = true to climberId
    }

    fun sortClimberList(climbers: List<ClimberRealm>) = when (sortOption.value) {
        ClimberSortOption.ID -> climbers.sortedBy { it.id.toIntOrNull() }
        ClimberSortOption.NAME -> climbers.sortedBy { it.name }
        ClimberSortOption.YEAR -> climbers.sortedBy { it.dateOfBirth }
        ClimberSortOption.COUNTRY -> climbers.sortedBy { it.country }
    }

    fun updateListDisplay() {
        isSortDropdownExpanded.value = false
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

        climberList =
            sortClimberList(filteredClimberList)//.filter { database.getSpeedResultsByClimberId(it.id).size > 20 }
    }

    fun deleteUser(climberId: String) = coroutineScope.launch {
        database.deleteClimber(climberId)
        updateListDisplay()
    }

    if (!initDone.value) {
        initDone.value = true
        updateListDisplay()
    }

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

    if (importMode.value != null) {
        val mode = importMode.value
        val file = FileHelper().selectCsvFile()
        importMode.value = null
        isImportDropdownExpanded.value = false
        if (file != null) {
            coroutineScope.launch {
                try {
                    when (mode) {
                        InputDataType.CLIMBER -> {
                            val climbers = CsvHelper().readClimbers(file.path)
                            climbers.forEach {
                                database.writeClimber(it)
                            }
                        }

                        InputDataType.SPEED -> {
                            val speeds = CsvHelper().readSpeeds(file.path)
                            database.writeSpeedResults(speeds)
                        }

                        InputDataType.LEAD -> {
                            val leads = CsvHelper().readLeads(file.path)
                            database.writeLeadResults(leads)
                        }

                        InputDataType.BOULDER -> {
                            val boulders = CsvHelper().readBoulders(file.path)
                            database.writeBoulderResults(boulders)
                        }

                        null -> return@launch
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
        modifier = Modifier.background(AppColors.Gray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TopAppBar(
            title = {
                Text("Zawodnicy")
            },
            backgroundColor = AppColors.Blue,
            modifier = Modifier.height(70.dp),
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
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
                        val boulders = database.getAllBoulders()
                        val leads = database.getAllLeads()
                        val speeds = database.getAllSpeeds()
                        val path: Path = CsvHelper().writeClimbers(climbers)
                        CsvHelper().writeBoulders(boulders)
                        CsvHelper().writeLeads(leads)
                        CsvHelper().writeSpeeds(speeds)
                        withContext(Dispatchers.IO) {
                            val os = System.getProperty("os.name").toLowerCase()
                            if (os.startsWith("windows")) {
                                Runtime.getRuntime().exec("explorer.exe /select,$path")
                            } else if (os.startsWith("mac")) {
                                Runtime.getRuntime().exec("open -R $path")
                            } else if (os.startsWith("linux")) {
                                Runtime.getRuntime().exec("nautilus $path")
                            }
                        }
                    }
                }) {
                    Icon(
                        painter = painterResource("export.svg"),
                        contentDescription = null,
                        modifier = Modifier.height(35.dp),
                    )
                }
                IconButton(onClick = { isImportDropdownExpanded.value = true }) {
                    Icon(
                        painter = painterResource("import.svg"),
                        contentDescription = null,
                        modifier = Modifier.height(35.dp),
                    )
                }
                DropdownMenu(
                    expanded = isImportDropdownExpanded.value,
                    onDismissRequest = { isImportDropdownExpanded.value = false }
                ) {
                    Button(onClick = { importMode.value = InputDataType.CLIMBER }) {
                        Text("Zawodnicy")
                    }
                    Button(onClick = { importMode.value = InputDataType.SPEED }) {
                        Text("SPEED")
                    }
                    Button(onClick = { importMode.value = InputDataType.BOULDER }) {
                        Text("BOULDER")
                    }
                    Button(onClick = { importMode.value = InputDataType.LEAD }) {
                        Text("LEAD")
                    }
                }
                IconButton(onClick = { isFilterDropdownExpanded.value = true }) {
                    Icon(
                        painter = painterResource("filter.svg"),
                        contentDescription = null,
                        modifier = Modifier.height(35.dp),
                    )
                }
                DropdownMenu(
                    expanded = isFilterDropdownExpanded.value,
                    onDismissRequest = {
                        isFilterDropdownExpanded.value = false
                        updateListDisplay()
                    },
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
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
                                    },
                                )
                            }
                        }
                    }
                }
                IconButton(onClick = { isSortDropdownExpanded.value = true }) {
                    Icon(
                        painter = painterResource("sort.svg"),
                        contentDescription = null,
                        modifier = Modifier.height(35.dp),
                    )
                }
                DropdownMenu(
                    expanded = isSortDropdownExpanded.value,
                    onDismissRequest = { isSortDropdownExpanded.value = false },
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Data urodzenia"
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.clickable { isDeletingEnabled.value = !isDeletingEnabled.value },
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.height(35.dp),
                    )
                    Switch(
                        checked = isDeletingEnabled.value,
                        onCheckedChange = { isDeletingEnabled.value = !isDeletingEnabled.value },
                        modifier = Modifier.height(20.dp)
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
        // The LazyColumn will be our table.
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            // Here is the header
            item {
                Row(Modifier.background(Color.Gray)) {
                    TableCell(text = "Id", weight = column1Weight)
                    TableCell(text = "Imię i nazwisko", weight = column2Weight)
                    TableCell(text = "Płeć", weight = column3Weight)
                    TableCell(text = "Data urodzenia", weight = column4Weight)
                    TableCell(text = "Kraj", weight = column5Weight)
                    TableCell(text = "Edytuj", weight = column6Weight)
                    if (isDeletingEnabled.value) {
                        TableCell(text = "Usuń", weight = column6Weight)
                    }
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
                    TableCell(text = it.dateOfBirth ?: "-", weight = column4Weight)
                    TableCell(text = it.country, weight = column5Weight)
                    TableCell(
                        image = Icons.Default.Edit,
                        weight = column6Weight,
                        onClick = { showEditClimberDialog(it.id) })
                    if (isDeletingEnabled.value) {
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

enum class InputDataType {
    CLIMBER,
    SPEED,
    LEAD,
    BOULDER,
}
