package ui.feature.climberdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.realm.Database
import kotlinx.coroutines.CoroutineScope
import scraping.Scraper
import scraping.model.Climber
import scraping.model.RecordType
import ui.common.Dialog
import ui.common.TableCell
import ui.feature.climberdetails.ChartType.SPEED_PROGRESS_COMPARATIVE
import ui.feature.climberdetails.ChartType.SPEED_PROGRESS_INDIVIDUAL
import ui.feature.climberdetails.ContentType.ANALYSIS
import ui.feature.climberdetails.ContentType.BOULDER
import ui.feature.climberdetails.ContentType.LEAD
import ui.feature.climberdetails.ContentType.SPEED
import ui.feature.climberdetails.chart.SpeedProgressComparativeChart
import ui.feature.climberdetails.chart.SpeedProgressIndividualChart
import utils.AppColors
import utils.ImageLoader

@Composable
fun ClimberDetailsScreen(
    climber: Climber?,
    onBackClick: () -> Unit,
    database: Database,
    coroutineScope: CoroutineScope,
) {

    if (climber == null) return

    val leadResults = remember { mutableStateOf(database.getLeadResultsByClimberId(climber.climberId)) }
    val speedResults = remember { mutableStateOf(database.getSpeedResultsByClimberId(climber.climberId)) }
    val boulderResults = remember { mutableStateOf(database.getBoulderResultsByClimberId(climber.climberId)) }

    val selectedResultType = remember {
        mutableStateOf(
            if (speedResults.value.isNotEmpty()) SPEED
            else if (leadResults.value.isNotEmpty()) LEAD
            else if (boulderResults.value.isNotEmpty()) BOULDER
            else null
        )
    }

    val isDropdownExpanded = remember { mutableStateOf(false) }

    val isAddDialogVisible = remember { mutableStateOf(false) }

    val chartSelected = remember { mutableStateOf<ChartType?>(null) }

    val isClimberDataEditable = climber.recordType == RecordType.UNOFFICIAL

    @Composable
    fun LeadTable() {
        val weight1 = .1f
        val weight2 = .2f
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Row(Modifier.background(Color.Gray)) {
                    TableCell(text = "Data", weight = weight1)
                    TableCell(text = "Pozycja w zawodach", weight = weight1)
                    TableCell(text = "Miasto", weight = weight2)
                    TableCell(text = "Nazwa zawodów", weight = weight2)
                    TableCell(text = "Kwalifikacje", weight = weight2)
                    TableCell(text = "Półfinał", weight = weight2)
                    TableCell(text = "Finał", weight = weight2)
                }
            }
            items(leadResults.value) {
                Row(Modifier.fillMaxWidth()) {
                    TableCell(text = it.date, weight = weight1)
                    TableCell(text = it.rank?.toString() ?: "-", weight = weight1)
                    TableCell(text = it.competitionCity, weight = weight2)
                    TableCell(text = it.competitionTitle, weight = weight2)
                    TableCell(text = it.qualification, weight = weight2)
                    TableCell(text = it.semiFinal ?: "-", weight = weight2)
                    TableCell(text = it.final ?: "-", weight = weight2)
                }
            }

        }
    }

    @Composable
    fun SpeedTable() {
        val weight1 = .1f
        val weight2 = .2f
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Row(Modifier.background(Color.Gray)) {
                    TableCell(text = "", weight = (weight1 + weight2) * 2)
                    TableCell(text = "Kwalifikacje", weight = weight1 * 2)
                    TableCell(text = "Finały", weight = weight1 * 5)
                }
                Row(Modifier.background(Color.Gray)) {
                    TableCell(text = "Data", weight = weight1)
                    TableCell(text = "Pozycja w zawodach", weight = weight1)
                    TableCell(text = "Miasto", weight = weight2)
                    TableCell(text = "Nazwa zawodów", weight = weight2)
                    TableCell(text = "Tor A", weight = weight1)
                    TableCell(text = "Tor B", weight = weight1)
                    TableCell(text = "1/8", weight = weight1)
                    TableCell(text = "1/4", weight = weight1)
                    TableCell(text = "Półfinał", weight = weight1)
                    TableCell(text = "Mały finał", weight = weight1)
                    TableCell(text = "Finał", weight = weight1)
                }
            }
            items(speedResults.value) {
                Row(Modifier.fillMaxWidth()) {
                    TableCell(text = it.date, weight = weight1)
                    TableCell(text = it.rank?.toString() ?: "-", weight = weight1)
                    TableCell(text = it.competitionCity, weight = weight2)
                    TableCell(text = it.competitionTitle, weight = weight2)
                    TableCell(text = it.laneA ?: "-", weight = weight1)
                    TableCell(text = it.laneB ?: "-", weight = weight1)
                    TableCell(text = it.oneEighth ?: "-", weight = weight1)
                    TableCell(text = it.quarter ?: "-", weight = weight1)
                    TableCell(text = it.semiFinal ?: "-", weight = weight1)
                    TableCell(text = it.smallFinal ?: "-", weight = weight1)
                    TableCell(text = it.final ?: "-", weight = weight1)
                }
            }

        }
    }

    @Composable
    fun BoulderTable() {
        val weight1 = .1f
        val weight2 = .2f
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Row(Modifier.background(Color.Gray)) {
                    TableCell(text = "Data", weight = weight1)
                    TableCell(text = "Pozycja w zawodach", weight = weight1)
                    TableCell(text = "Miasto", weight = weight2)
                    TableCell(text = "Nazwa zawodów", weight = weight2)
                    TableCell(text = "Kwalifikacje", weight = weight2)
                    TableCell(text = "Półfinał", weight = weight2)
                    TableCell(text = "Finał", weight = weight2)
                }
            }
            items(boulderResults.value) {
                Row(Modifier.fillMaxWidth()) {
                    TableCell(text = it.date, weight = weight1)
                    TableCell(text = it.rank?.toString() ?: "-", weight = weight1)
                    TableCell(text = it.competitionCity, weight = weight2)
                    TableCell(text = it.competitionTitle, weight = weight2)
                    TableCell(text = it.qualification, weight = weight2)
                    TableCell(text = it.semiFinal ?: "-", weight = weight2)
                    TableCell(text = it.final ?: "-", weight = weight2)
                }
            }

        }
    }

    @Composable
    fun Analysis() {
        when (chartSelected.value) {
            SPEED_PROGRESS_INDIVIDUAL -> SpeedProgressIndividualChart(speedResults.value.sortedBy { it.date })
            SPEED_PROGRESS_COMPARATIVE -> SpeedProgressComparativeChart(climber.climberId, database)
            else -> {}
        }
    }

    @Composable
    fun SelectContentButton(onClick: () -> Unit, text: String) {
        Button(
            onClick = onClick
        ) {
            Text(text)
        }
    }

    Column(
        modifier = Modifier.background(AppColors.Gray).fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Informacje o zawodniku"
                )
            },
            backgroundColor = AppColors.Blue,
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { isAddDialogVisible.value = true }
                ) {
                    if (isClimberDataEditable) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                        )
                    }
                }
            }
        )
        Spacer(Modifier.height(10.dp))
        Row {
            Column {
                val bitmap = ImageLoader.loadImageOfClimber(climber.imageUrl)
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        modifier = Modifier.requiredHeightIn(max = 150.dp).border(1.dp, AppColors.Blue),
                    )
                } else (
                        Image(
                            painter = painterResource("climber-default.jpg"),
                            contentDescription = null,
                            modifier = Modifier.requiredHeightIn(max = 150.dp).border(1.dp, AppColors.Blue),
                        )
                        )
            }
            Spacer(Modifier.width(20.dp))
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text("${climber.name} (Id: ${climber.climberId})")
                }
                Row {
                    Text(
                        text = "Liczba zawodów typu bouldering: ${boulderResults.value.size}"
                    )
                }
                Row {
                    Text(
                        text = "Liczba zawodów typu prowadzenie: ${leadResults.value.size}"
                    )
                }
                Row {
                    Text(
                        text = "Liczba zawodów typu prędkość: ${speedResults.value.size}"
                    )
                }

                if (isAddDialogVisible.value) {
                    Dialog(
                        title = "Dodawanie wyniku zawodnika",
                        content = DialogContentAddResult(
                            database = database,
                            coroutineScope = coroutineScope,
                            climberId = climber.climberId,
                            defaultResultType = selectedResultType.value
                        ) {
                            isAddDialogVisible.value = false
                            speedResults.value = database.getSpeedResultsByClimberId(climber.climberId)
                            leadResults.value = database.getLeadResultsByClimberId(climber.climberId)
                            boulderResults.value = database.getBoulderResultsByClimberId(climber.climberId)
                        },
                        onCloseRequest = { isAddDialogVisible.value = false },
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    if (leadResults.value.isNotEmpty()) {
                        SelectContentButton(
                            onClick = { selectedResultType.value = LEAD },
                            text = "LEAD"
                        )
                    }
                    if (speedResults.value.isNotEmpty()) {
                        SelectContentButton(
                            onClick = { selectedResultType.value = SPEED },
                            text = "SPEED"
                        )
                    }
                    if (boulderResults.value.isNotEmpty()) {
                        SelectContentButton(
                            onClick = { selectedResultType.value = BOULDER },
                            text = "BOULDER"
                        )
                    }
                }
                Row {
                    if (leadResults.value.isNotEmpty() || speedResults.value.isNotEmpty() || boulderResults.value.isNotEmpty()) {
                        SelectContentButton(
                            onClick = { selectedResultType.value = ANALYSIS },
                            text = "Analiza wyników"
                        )
                        Column {
                            IconButton(onClick = { isDropdownExpanded.value = true }) {
                                Icon(Icons.Default.MoreVert, "")
                            }
                            DropdownMenu(
                                expanded = isDropdownExpanded.value,
                                onDismissRequest = { isDropdownExpanded.value = false }
                            ) {
                                Button(onClick = { chartSelected.value = SPEED_PROGRESS_INDIVIDUAL }) {
                                    Text("Postęp w kategorii SPEED")
                                }
                                Button(onClick = { chartSelected.value = SPEED_PROGRESS_COMPARATIVE }) {
                                    Text("Porównanie w kategorii SPEED")
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))

        when (selectedResultType.value) {
            LEAD -> LeadTable()
            SPEED -> SpeedTable()
            BOULDER -> BoulderTable()
            ANALYSIS -> Analysis()
        }
    }
}

enum class ChartType {
    SPEED_PROGRESS_INDIVIDUAL,
    SPEED_PROGRESS_COMPARATIVE,
}

object ContentType {
    const val LEAD = Scraper.LEAD
    const val SPEED = Scraper.SPEED
    const val BOULDER = Scraper.BOULDER
    const val ANALYSIS = "ANALYSIS"
}
