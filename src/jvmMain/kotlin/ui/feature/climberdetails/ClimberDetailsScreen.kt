package ui.feature.climberdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.realm.Database
import io.realm.model.BoulderResultRealm
import io.realm.model.LeadResultRealm
import io.realm.model.SpeedResultRealm
import scraping.Scraper
import ui.common.TableCell
import ui.feature.climberdetails.ChartType.SPEED_PROGRESS_INDIVIDUAL
import ui.feature.climberdetails.ChartType.SPEED_PROGRESS_COMPARATIVE
import ui.feature.climberdetails.ContentType.ANALYSIS
import ui.feature.climberdetails.ContentType.BOULDER
import ui.feature.climberdetails.ContentType.LEAD
import ui.feature.climberdetails.ContentType.SPEED
import ui.feature.climberdetails.chart.SpeedProgressComparativeChart
import ui.feature.climberdetails.chart.SpeedProgressIndividualChart

@Composable
fun ClimberDetailsScreen(
    climberId: String,
    onBackClick: () -> Unit,
    leadResults: List<LeadResultRealm>,
    speedResults: List<SpeedResultRealm>,
    boulderResults: List<BoulderResultRealm>,
    database: Database,
) {

    val selectedResultType = remember { mutableStateOf<String?>(null) }

    val isDropdownExpanded = remember { mutableStateOf(false) }

    val chartSelected = remember { mutableStateOf<ChartType?>(null) }

    @Composable
    fun LeadTable() {
        val weight1 = .1f
        val weight2 = .2f
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Row(Modifier.background(Color.Gray)) {
                    TableCell(text = "Rok", weight = weight1)
                    TableCell(text = "Pozycja w zawodach", weight = weight1)
                    TableCell(text = "Kwalifikacje", weight = weight2)
                    TableCell(text = "Półfinał", weight = weight2)
                    TableCell(text = "Finał", weight = weight2)
                }
            }
            items(leadResults) {
                Row(Modifier.fillMaxWidth()) {
                    TableCell(text = it.year.toString(), weight = weight1)
                    TableCell(text = it.rank?.toString() ?: "-", weight = weight1)
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
                    TableCell(text = "", weight = weight1 * 2)
                    TableCell(text = "Kwalifikacje", weight = weight1 * 2)
                    TableCell(text = "Finały", weight = weight1 * 3 + weight2 * 2)
                }
                Row(Modifier.background(Color.Gray)) {
                    TableCell(text = "Rok", weight = weight1)
                    TableCell(text = "Pozycja w zawodach", weight = weight1)
                    TableCell(text = "Tor A", weight = weight1)
                    TableCell(text = "Tor B", weight = weight1)
                    TableCell(text = "1/8", weight = weight1)
                    TableCell(text = "1/4", weight = weight1)
                    TableCell(text = "Półfinał", weight = weight1)
                    TableCell(text = "Mały finał", weight = weight2)
                    TableCell(text = "Finał", weight = weight2)
                }
            }
            items(speedResults) {
                Row(Modifier.fillMaxWidth()) {
                    TableCell(text = it.year.toString(), weight = weight1)
                    TableCell(text = it.rank?.toString() ?: "-", weight = weight1)
                    TableCell(text = it.laneA ?: "-", weight = weight1)
                    TableCell(text = it.laneB ?: "-", weight = weight1)
                    TableCell(text = it.oneEighth ?: "-", weight = weight1)
                    TableCell(text = it.quarter ?: "-", weight = weight1)
                    TableCell(text = it.semiFinal ?: "-", weight = weight1)
                    TableCell(text = it.smallFinal ?: "-", weight = weight2)
                    TableCell(text = it.final ?: "-", weight = weight2)
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
                    TableCell(text = "Rok", weight = weight1)
                    TableCell(text = "Pozycja w zawodach", weight = weight1)
                    TableCell(text = "Kwalifikacje", weight = weight2)
                    TableCell(text = "Półfinał", weight = weight2)
                    TableCell(text = "Finał", weight = weight2)
                }
            }
            items(boulderResults) {
                Row(Modifier.fillMaxWidth()) {
                    TableCell(text = it.year.toString(), weight = weight1)
                    TableCell(text = it.rank?.toString() ?: "-", weight = weight1)
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
            SPEED_PROGRESS_INDIVIDUAL -> SpeedProgressIndividualChart(speedResults.sortedBy { it.year })
            SPEED_PROGRESS_COMPARATIVE -> SpeedProgressComparativeChart(climberId, database)
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

    MaterialTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "Informacje o zawodniku"
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
                horizontalArrangement = Arrangement.Center,
            ) {
                Text("Informacje o zawodniku z id: $climberId")
            }
            Row {
                Text(
                    text = "Boulder: ${boulderResults.size}"
                )
            }
            Row {
                Text(
                    text = "Lead: ${leadResults.size}"
                )
            }
            Row {
                Text(
                    text = "Speed: ${speedResults.size}"
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                if (leadResults.isNotEmpty()) {
                    SelectContentButton(
                        onClick = { selectedResultType.value = LEAD },
                        text = "LEAD"
                    )
                }
                if (speedResults.isNotEmpty()) {
                    SelectContentButton(
                        onClick = { selectedResultType.value = SPEED },
                        text = "SPEED"
                    )
                }
                if (boulderResults.isNotEmpty()) {
                    SelectContentButton(
                        onClick = { selectedResultType.value = BOULDER },
                        text = "BOULDER"
                    )
                }
            }
            Row {
                if (leadResults.isNotEmpty() || speedResults.isNotEmpty() || boulderResults.isNotEmpty()) {
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
                            Button(onClick = {chartSelected.value = SPEED_PROGRESS_INDIVIDUAL}) {
                                Text("Postęp w kategorii SPEED")
                            }
                            Button(onClick = {chartSelected.value = SPEED_PROGRESS_COMPARATIVE}) {
                                Text("Porównanie w kategorii SPEED")
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
