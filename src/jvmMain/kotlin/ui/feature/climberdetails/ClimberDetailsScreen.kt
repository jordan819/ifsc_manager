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
        val column1Weight = .1f
        val column2Weight = .1f
        val column3Weight = .2f
        val column4Weight = .2f
        val column5Weight = .2f
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Row(Modifier.background(Color.Gray)) {
                    TableCell(text = "Rok", weight = column1Weight)
                    TableCell(text = "Pozycja w zawodach", weight = column2Weight)
                    TableCell(text = "Kwalifikacje", weight = column3Weight)
                    TableCell(text = "Półfinał", weight = column4Weight)
                    TableCell(text = "Finał", weight = column5Weight)
                }
            }
            items(leadResults) {
                Row(Modifier.fillMaxWidth()) {
                    TableCell(text = it.year.toString(), weight = column1Weight)
                    TableCell(text = it.rank?.toString() ?: "-", weight = column2Weight)
                    TableCell(text = it.qualification, weight = column3Weight)
                    TableCell(text = it.semiFinal ?: "-", weight = column4Weight)
                    TableCell(text = it.final ?: "-", weight = column5Weight)
                }
            }

        }
    }

    @Composable
    fun SpeedTable() {
        Text("Tabela SPEED")
    }

    @Composable
    fun BoulderTable() {
        Text("Tabela Boulder")
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
