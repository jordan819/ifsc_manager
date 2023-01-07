package ui.feature.climberdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import kotlinx.coroutines.CoroutineScope
import scraping.Scraper.Companion.BOULDER
import scraping.Scraper.Companion.LEAD
import scraping.Scraper.Companion.SPEED
import ui.common.TableCell

@Composable
fun ClimberDetailsScreen(
    climberId: String,
    database: Database,
    onBackClick: () -> Unit,
    coroutineScope: CoroutineScope,
    leadResults: List<LeadResultRealm>,
    speedResults: List<SpeedResultRealm>,
    boulderResults: List<BoulderResultRealm>,
) {

    val selectedResultType = remember { mutableStateOf<String?>(null) }

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
    fun SelectTypeButton(onClick: () -> Unit, text: String) {
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
                    SelectTypeButton(
                        onClick = { selectedResultType.value = LEAD },
                        text = "LEAD"
                    )
                }
                if (speedResults.isNotEmpty()) {
                    SelectTypeButton(
                        onClick = { selectedResultType.value = SPEED },
                        text = "SPEED"
                    )
                }
                if (boulderResults.isNotEmpty()) {
                    SelectTypeButton(
                        onClick = { selectedResultType.value = BOULDER },
                        text = "BOULDER"
                    )
                }
            }
            Spacer(Modifier.height(10.dp))

            when (selectedResultType.value) {
                LEAD -> LeadTable()
                SPEED -> SpeedTable()
                BOULDER -> BoulderTable()
            }
        }
    }

}
