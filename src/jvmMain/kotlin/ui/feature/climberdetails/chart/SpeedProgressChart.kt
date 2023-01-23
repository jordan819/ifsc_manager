package ui.feature.climberdetails.chart

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.realm.model.SpeedResultRealm
import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.XYChart
import org.knowm.xchart.XYChartBuilder
import org.knowm.xchart.style.markers.SeriesMarkers
import java.awt.Dimension
import java.awt.Toolkit

@Composable
fun SpeedProgressChart(
    speedResults: List<SpeedResultRealm>,
) {
    val oneEightResult = mutableListOf<Double?>()
    val quarterResult = mutableListOf<Double?>()
    val semiResult = mutableListOf<Double?>()
    val smallResult = mutableListOf<Double?>()
    val finalResult = mutableListOf<Double?>()
    speedResults.forEach { result: SpeedResultRealm ->
        oneEightResult.add(result.oneEighth?.toDoubleOrNull())
        quarterResult.add(result.quarter?.toDoubleOrNull())
        semiResult.add(result.semiFinal?.toDoubleOrNull())
        smallResult.add(result.smallFinal?.toDoubleOrNull())
        finalResult.add(result.final?.toDoubleOrNull())
    }
    val xLabels = (1..oneEightResult.size).toList()

    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    val width: Double = screenSize.getWidth()

    val chart = XYChartBuilder()
        .xAxisTitle("Index")
        .yAxisTitle("Wynik")
        .width(width.toInt())
        .build()

    chart.addSeries("1/8", xLabels, oneEightResult).marker = SeriesMarkers.CIRCLE
    addChartSeries(chart, "1/4", quarterResult)
    addChartSeries(chart, "Semi Final", semiResult)
    addChartSeries(chart, "Small Final", smallResult)
    addChartSeries(chart, "Final", finalResult)

    val image = BitmapEncoder.getBufferedImage(chart).toComposeImageBitmap()

    Column {
        Text("Speed Progress Chart")
        Image(
            bitmap = image,
            contentDescription = null,
        )
    }

}

private fun addChartSeries(chart: XYChart, name: String, results: List<Double?>) {
    if (results.any { it != null }) {
        chart.addSeries(name, results)
    }
}
