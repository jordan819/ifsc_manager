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
import utils.DateUtils
import java.awt.Toolkit
import java.util.*


@Composable
fun ProgressByClimberAge(
    climberId: String,
    database: Database,
) {

    val stageSelected = remember { mutableStateOf(Stage.QUALIFICATIONS) }

    val climber = database.getClimberById(climberId)
    val climberBirthDate = DateUtils.formatDate(climber?.dateOfBirth)
    val results = database.getSpeedResultsByClimberId(climberId).groupBy {
        val date = it.date.split("-")
        date[0] + "-" + date[1]
    }.toSortedMap(compareBy { it })

    if (climberBirthDate == null) {
        Spacer(Modifier.height(50.dp))
        Text(
            color = Color.Red,
            text = "Brak informacji o wieku zawodnika!"
        )
    } else {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val width = screenSize.getWidth()
        val chart = XYChartBuilder()
            .xAxisTitle("Data")
            .yAxisTitle("Czas")
            .width(width.toInt())
            .build()
        chart.styler.datePattern = "MM-yyyy"
        val title: String
        val xData = mutableListOf<Date>()
        val yData = mutableListOf<Double>()
        when (stageSelected.value) {
            Stage.QUALIFICATIONS -> {
                title = "Kwalifikacje"
                results.forEach { result ->
                    val values = mutableListOf<Double?>()
                    result.value.forEach {
                        values.add(it.laneA?.toDoubleOrNull())
                        values.add(it.laneB?.toDoubleOrNull())
                    }
                    if (values.filterNotNull().isNotEmpty()) {
                        DateUtils.formatDate(result.key)?.let { xData.add(it) }
                        yData.add(values.filterNotNull().average())
                    }
                }
            }

            Stage.ONE_EIGHT -> {
                title = "1/8"
                results.forEach { result ->
                    val values = mutableListOf<Double?>()
                    result.value.forEach {
                        values.add(it.oneEighth?.toDoubleOrNull())
                    }
                    if (values.filterNotNull().isNotEmpty()) {
                        DateUtils.formatDate(result.key)?.let { xData.add(it) }
                        yData.add(values.filterNotNull().average())
                    }
                }
            }

            Stage.QUARTER -> {
                title = "Ćwierćfinał"
                results.forEach { result ->
                    val values = mutableListOf<Double?>()
                    result.value.forEach {
                        values.add(it.quarter?.toDoubleOrNull())
                    }
                    if (values.filterNotNull().isNotEmpty()) {
                        DateUtils.formatDate(result.key)?.let { xData.add(it) }
                        yData.add(values.filterNotNull().average())
                    }
                }
            }

            Stage.SEMI_FINAL -> {
                title = "Półfinał"
                results.forEach { result ->
                    val values = mutableListOf<Double?>()
                    result.value.forEach {
                        values.add(it.semiFinal?.toDoubleOrNull())
                    }
                    if (values.filterNotNull().isNotEmpty()) {
                        DateUtils.formatDate(result.key)?.let { xData.add(it) }
                        yData.add(values.filterNotNull().average())
                    }
                }
            }

            Stage.FINALS -> {
                title = "Finały"
                results.forEach { result ->
                    val values = mutableListOf<Double?>()
                    result.value.forEach {
                        values.add(it.smallFinal?.toDoubleOrNull())
                        values.add(it.final?.toDoubleOrNull())
                    }
                    if (values.filterNotNull().isNotEmpty()) {
                        DateUtils.formatDate(result.key)?.let { xData.add(it) }
                        yData.add(values.filterNotNull().average())
                    }
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .clickable { stageSelected.value = Stage.QUALIFICATIONS }
                    .background(AppColors.Blue)
                    .padding(8.dp)
            ) {
                Text(text = "Kwalifikacje", color = Color.White, fontSize = 14.sp)
            }
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
        if (xData.size < 2) {
            Spacer(Modifier.height(50.dp))
            Text(
                color = Color.Red,
                text = "Brak danych do wyświetlenia!"
            )
        } else {
            chart.title = title
            chart.addSeries("Średni wynik", xData, yData)
            Image(
                bitmap = BitmapEncoder.getBufferedImage(chart).toComposeImageBitmap(),
                contentDescription = null,
            )
        }
    }

}
