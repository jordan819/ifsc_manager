package ui.feature.climberdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.realm.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import scraping.Scraper
import scraping.model.Climber
import scraping.model.RecordType
import ui.common.Dialog
import ui.common.TableCell
import ui.feature.climberdetails.ChartType.*
import ui.feature.climberdetails.ContentType.ANALYSIS
import ui.feature.climberdetails.ContentType.BOULDER
import ui.feature.climberdetails.ContentType.LEAD
import ui.feature.climberdetails.ContentType.SPEED
import ui.feature.climberdetails.chart.SpeedProgressComparativeChart
import ui.feature.climberdetails.chart.SpeedProgressIndividualChart
import ui.feature.climberdetails.chart.SuccessFailRatioPercentageChart
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

    val isImageLoading = remember { mutableStateOf(true) }
    val bitmap = remember { mutableStateOf<ImageBitmap?>(null) }
    coroutineScope.launch {
        delay(500)
        if (climber.imageUrl.isNotBlank()) {
            bitmap.value = ImageLoader.loadImageOfClimber(climber.imageUrl)
        }
        isImageLoading.value = false
    }

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

    val isEditDialogVisible = remember { mutableStateOf(false to "") }
    fun showEditDialog(resultId: String) {
        isEditDialogVisible.value = true to resultId
    }

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
                Row(Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isClimberDataEditable) {
                            showEditDialog(it.id)
                        }
                    }) {
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
            SUCCESS_FAIL_RATIO -> SuccessFailRatioPercentageChart(climber.climberId, database)
            else -> {}
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
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                bitmap.value.let {
                    if (isImageLoading.value) {
                        Column(
                            modifier = Modifier.width(200.dp).height(200.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            CircularProgressIndicator(color = AppColors.Blue)
                        }
                    } else if (it != null) {
                        Image(
                            bitmap = it,
                            contentDescription = null,
                            modifier = Modifier.requiredHeightIn(max = 200.dp).border(1.dp, AppColors.Blue),
                        )
                    } else {
                        Image(
                            painter = painterResource("climber-default.jpg"),
                            contentDescription = null,
                            modifier = Modifier.requiredHeightIn(max = 200.dp).border(1.dp, AppColors.Blue),
                        )
                    }
                }
            }
            Spacer(Modifier.width(20.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text("${climber.name} (Id: ${climber.climberId})")
                }
                Row {
                    Text(
                        text = "Zawody typu czas: ${speedResults.value.size}"
                    )
                }
                Row {
                    Text(
                        text = "Zawody typu prowadzenie: ${leadResults.value.size}"
                    )
                }
                Row {
                    Text(
                        text = "Zawody typu bouldering: ${boulderResults.value.size}"
                    )
                }

                if (isAddDialogVisible.value) {
                    Dialog(
                        title = "Dodawanie wyniku zawodnika",
                        content = DialogContentAddResult(
                            database = database,
                            coroutineScope = coroutineScope,
                            climberId = climber.climberId,
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
                    if (speedResults.value.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .clickable { selectedResultType.value = SPEED }
                                .background(AppColors.Blue)
                                .padding(8.dp)
                        ) {
                            Text(text = "Czas", color = Color.White, fontSize = 14.sp)
                        }
                    }
                    if (leadResults.value.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .clickable { selectedResultType.value = LEAD }
                                .background(AppColors.Blue)
                                .padding(8.dp)
                        ) {
                            Text(text = "Prowadzenie", color = Color.White, fontSize = 14.sp)
                        }
                    }
                    if (boulderResults.value.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .clickable { selectedResultType.value = BOULDER }
                                .background(AppColors.Blue)
                                .padding(8.dp)
                        ) {
                            Text(text = "Bouldering", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (speedResults.value.isNotEmpty()) {
                        Text("Analiza wyników")
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            IconButton(onClick = {
                                isDropdownExpanded.value = true
                            }) {
                                Icon(Icons.Default.ArrowDropDown, "")
                            }
                            DropdownMenu(
                                expanded = isDropdownExpanded.value,
                                onDismissRequest = { isDropdownExpanded.value = false },
                                modifier = Modifier.padding(5.dp),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clickable {
                                            chartSelected.value = SPEED_PROGRESS_INDIVIDUAL
                                            selectedResultType.value = ANALYSIS
                                        }
                                        .align(Alignment.CenterHorizontally)
                                        .background(AppColors.Blue)
                                        .padding(8.dp)
                                ) {
                                    Text(text = "Postęp indywidualny", color = Color.White, fontSize = 14.sp)
                                }
                                Spacer(Modifier.height(5.dp))
                                Row(
                                    modifier = Modifier
                                        .clickable {
                                            chartSelected.value = SPEED_PROGRESS_COMPARATIVE
                                            selectedResultType.value = ANALYSIS
                                        }
                                        .align(Alignment.CenterHorizontally)
                                        .background(AppColors.Blue)
                                        .padding(8.dp)
                                ) {
                                    Text(text = "Porównanie z konkurencją", color = Color.White, fontSize = 14.sp)
                                }
                                Spacer(Modifier.height(5.dp))
                                Row(
                                    modifier = Modifier
                                        .clickable {
                                            chartSelected.value = SUCCESS_FAIL_RATIO
                                            selectedResultType.value = ANALYSIS
                                        }
                                        .align(Alignment.CenterHorizontally)
                                        .background(AppColors.Blue)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "Udział dyskwalifikacji we wszystkich startach",
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
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
        if (isEditDialogVisible.value.first) {
            Dialog(
                title = "Aktualizacja wyniku",
                content = DialogContentEditResult(
                    database = database,
                    coroutineScope = coroutineScope,
                    resultId = isEditDialogVisible.value.second,
                ) {
                    leadResults.value = database.getLeadResultsByClimberId(climber.climberId)
                    speedResults.value = database.getSpeedResultsByClimberId(climber.climberId)
                    boulderResults.value = database.getBoulderResultsByClimberId(climber.climberId)
                    isEditDialogVisible.value = false to "0"
                },
                onCloseRequest = { isEditDialogVisible.value = false to "0" },
            )
        }
    }
}

enum class ChartType {
    SPEED_PROGRESS_INDIVIDUAL,
    SPEED_PROGRESS_COMPARATIVE,
    SUCCESS_FAIL_RATIO,
}

object ContentType {
    const val LEAD = Scraper.LEAD
    const val SPEED = Scraper.SPEED
    const val BOULDER = Scraper.BOULDER
    const val ANALYSIS = "ANALYSIS"
}
