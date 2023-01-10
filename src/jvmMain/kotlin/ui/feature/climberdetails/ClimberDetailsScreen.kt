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
import kotlinx.coroutines.CoroutineScope
import scraping.Scraper
import ui.common.TableCell
import ui.feature.climberdetails.ContentType.ANALYSIS
import ui.feature.climberdetails.ContentType.BOULDER
import ui.feature.climberdetails.ContentType.LEAD
import ui.feature.climberdetails.ContentType.SPEED

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

    val isDropdownExpanded = remember { mutableStateOf(false) }

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
        Column {
            IconButton(onClick = { isDropdownExpanded.value = true }) {
                Icon(Icons.Default.MoreVert, "")
            }
            DropdownMenu(
                expanded = isDropdownExpanded.value,
                onDismissRequest = { isDropdownExpanded.value = false }
            ) {
                Button(onClick = {}) {
                    Text("val 1")
                }
                Button(onClick = {}) {
                    Text("val 2")
                }
                Button(onClick = {}) {
                    Text("val 3")
                }
                Button(onClick = {}) {
                    Text("val 4")
                }
            }
        }

//        val testX = Random.nextDouble(2010.0, 2020.0)
//        val xData = doubleArrayOf(testX, testX + 2, testX + 3, testX + 4, testX + 5, testX + 6)
//        val yData = doubleArrayOf(
//            Random.nextDouble(20.0, 30.0),
//            Random.nextDouble(20.0, 30.0),
//            Random.nextDouble(20.0, 30.0),
//            Random.nextDouble(20.0, 30.0),
//            Random.nextDouble(20.0, 30.0),
//            Random.nextDouble(20.0, 30.0),
//        )
//        val chart1 = QuickChart.getChart("Postęp zawodnika w czasie", "Rok", "Wynik", "Wynik od czasu", xData, yData)
//        val image1 = BitmapEncoder.getBufferedImage(chart1).toComposeImageBitmap()
//
//        val chart2 = PieChart(PieChartBuilder())
//        if (leadResults.isNotEmpty()) chart2.addSeries("LEAD", leadResults.size)
//        if (speedResults.isNotEmpty()) chart2.addSeries("SPEED", speedResults.size)
//        if (boulderResults.isNotEmpty()) chart2.addSeries("BOULDER", boulderResults.size)
//        val image2 = BitmapEncoder.getBufferedImage(chart2).toComposeImageBitmap()
//
//        val chart3 = CategoryChart(CategoryChartBuilder())
//        chart3.addSeries("1", intArrayOf(1, 2, 3), intArrayOf(4, 5, 6))
//        val image3 = BitmapEncoder.getBufferedImage(chart3).toComposeImageBitmap()
//
//        Row {
//            Image(
//                bitmap = image1,
//                contentDescription = null,
//            )
//        }
//        Row {
//            Image(
//                bitmap = image2,
//                contentDescription = null,
//            )
//        }
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
                if (leadResults.isNotEmpty() || speedResults.isNotEmpty() || boulderResults.isNotEmpty()) {
                    SelectContentButton(
                        onClick = { selectedResultType.value = ANALYSIS },
                        text = "Analiza wyników"
                    )
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

object ContentType {
    const val LEAD = Scraper.LEAD
    const val SPEED = Scraper.SPEED
    const val BOULDER = Scraper.BOULDER
    const val ANALYSIS = "ANALYSIS"
}
