package ui.feature.climberdetails.chart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.realm.Database
import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.XYChartBuilder
import utils.AppColors
import java.awt.Toolkit

@Composable
fun AverageTimeThresholds(
    database: Database,
) {

    val stageSelected = remember { mutableStateOf(Stage.ONE_EIGHT) }

    val resultsGroupedByYear = database.getAllSpeeds().groupBy { it.date.split("-").first() }

    val oneEight = mutableListOf<Pair<String, Double?>>()
    val quarter = mutableListOf<Pair<String, Double?>>()
    val semiFinal = mutableListOf<Pair<String, Double?>>()
    val finals = mutableListOf<Pair<String, Double?>>()

    resultsGroupedByYear.forEach { (date, results) ->
        val nonNullResults = mutableListOf<Double?>()
        results.forEach { result ->
            if (result.oneEighth != null) {
                result.laneA?.toDoubleOrNull().let {
                    if (it != null && it < 50) {
                        nonNullResults.add(it)
                    }
                }
                result.laneB?.toDoubleOrNull().let {
                    if (it != null && it < 50) {
                        nonNullResults.add(it)
                    }
                }
            }
        }
        oneEight.add(
            date to nonNullResults.filterNotNull().maxOrNull()
        )
    }

    resultsGroupedByYear.forEach { (date, results) ->
        val nonNullResults = mutableListOf<Double?>()
        results.forEach { result ->
            if (result.quarter != null) {
                result.oneEighth?.toDoubleOrNull().let {
                    if (it != null && it < 50) {
                        nonNullResults.add(it)
                    }
                }
            }
        }
        quarter.add(
            date to nonNullResults.filterNotNull().maxOrNull()
        )
    }

    resultsGroupedByYear.forEach { (date, results) ->
        val nonNullResults = mutableListOf<Double?>()
        results.forEach { result ->
            if (result.semiFinal != null) {
                result.quarter?.toDoubleOrNull().let {
                    if (it != null && it < 50) {
                        nonNullResults.add(it)
                    }
                }
            }
        }
        semiFinal.add(
            date to nonNullResults.filterNotNull().maxOrNull()
        )
    }

    resultsGroupedByYear.forEach { (date, results) ->
        val nonNullResults = mutableListOf<Double?>()
        results.forEach { result ->
            if (result.smallFinal != null || result.final != null) {
                result.semiFinal?.toDoubleOrNull().let {
                    if (it != null && it < 50) {
                        nonNullResults.add(it)
                    }
                }
            }
        }
        finals.add(
            date to nonNullResults.filterNotNull().maxOrNull()
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .clickable { stageSelected.value = Stage.ONE_EIGHT }
                    .background(AppColors.Blue)
                    .padding(8.dp)
            ) {
                Text(text = "1/8", color = Color.White, fontSize = 14.sp)
            }
            Row(
                modifier = Modifier
                    .clickable { stageSelected.value = Stage.QUARTER }
                    .background(AppColors.Blue)
                    .padding(8.dp)
            ) {
                Text(text = "Ćwierćfinał", color = Color.White, fontSize = 14.sp)
            }
            Row(
                modifier = Modifier
                    .clickable { stageSelected.value = Stage.SEMI_FINAL }
                    .background(AppColors.Blue)
                    .padding(8.dp)
            ) {
                Text(text = "Półfinał", color = Color.White, fontSize = 14.sp)
            }
            Row(
                modifier = Modifier
                    .clickable { stageSelected.value = Stage.FINALS }
                    .background(AppColors.Blue)
                    .padding(8.dp)
            ) {
                Text(text = "Finały", color = Color.White, fontSize = 14.sp)
            }
        }
        Spacer(Modifier.height(10.dp))
        when (stageSelected.value) {
            Stage.ONE_EIGHT -> {
                val screenSize = Toolkit.getDefaultToolkit().screenSize
                val width = screenSize.getWidth()
                val chart = XYChartBuilder()
                    .xAxisTitle("Rok")
                    .yAxisTitle("Czas")
                    .width(width.toInt())
                    .build()
                val yData = oneEight.map { it.second }
                val xData = oneEight.map { it.first.toDouble() }
                chart.addSeries("Najniższy wynik", xData, yData)
                chart.styler.xAxisTickMarkSpacingHint = 1
                chart.styler.xAxisDecimalPattern = "#"
                chart.title = "Progi czasowe 1/8"
                val image = BitmapEncoder.getBufferedImage(chart).toComposeImageBitmap()
                Image(
                    bitmap = image,
                    contentDescription = null,
                )
            }

            Stage.QUARTER -> {
                val screenSize = Toolkit.getDefaultToolkit().screenSize
                val width = screenSize.getWidth()
                val chart = XYChartBuilder()
                    .xAxisTitle("Rok")
                    .yAxisTitle("Czas")
                    .width(width.toInt())
                    .build()
                val yData = quarter.map { it.second }
                val xData = quarter.map { it.first.toDouble() }
                chart.addSeries("Najniższy wynik", xData, yData)
                chart.styler.xAxisTickMarkSpacingHint = 1
                chart.styler.xAxisDecimalPattern = "#"
                chart.title = "Progi czasowe ćwierćfinału"
                val image = BitmapEncoder.getBufferedImage(chart).toComposeImageBitmap()
                Image(
                    bitmap = image,
                    contentDescription = null,
                )
            }

            Stage.SEMI_FINAL -> {
                val screenSize = Toolkit.getDefaultToolkit().screenSize
                val width = screenSize.getWidth()
                val chart = XYChartBuilder()
                    .xAxisTitle("Rok")
                    .yAxisTitle("Czas")
                    .width(width.toInt())
                    .build()
                val yData = semiFinal.map { it.second }
                val xData = semiFinal.map { it.first.toDouble() }
                chart.addSeries("Najniższy wynik", xData, yData)
                chart.styler.xAxisTickMarkSpacingHint = 1
                chart.styler.xAxisDecimalPattern = "#"
                chart.title = "Progi czasowe półfinału"
                val image = BitmapEncoder.getBufferedImage(chart).toComposeImageBitmap()
                Image(
                    bitmap = image,
                    contentDescription = null,
                )
            }

            Stage.FINALS -> {
                val screenSize = Toolkit.getDefaultToolkit().screenSize
                val width = screenSize.getWidth()
                val chart = XYChartBuilder()
                    .xAxisTitle("Rok")
                    .yAxisTitle("Czas")
                    .width(width.toInt())
                    .build()
                val yData = finals.map { it.second }
                val xData = finals.map { it.first.toDouble() }
                chart.addSeries("Najniższy wynik", xData, yData)
                chart.styler.xAxisTickMarkSpacingHint = 1
                chart.styler.xAxisDecimalPattern = "#"
                chart.title = "Progi czasowe finałów"
                val image = BitmapEncoder.getBufferedImage(chart).toComposeImageBitmap()
                Image(
                    bitmap = image,
                    contentDescription = null,
                )
            }

            else -> {}
        }
    }
}


