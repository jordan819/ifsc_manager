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
import java.awt.Toolkit

@Composable
fun SpeedProgressIndividualChart(
    speedResults: List<SpeedResultRealm>,
) {
    val laneAResult = mutableListOf<Double?>()
    val laneBResult = mutableListOf<Double?>()
    val oneEightResult = mutableListOf<Double?>()
    val quarterResult = mutableListOf<Double?>()
    val semiResult = mutableListOf<Double?>()
    val smallResult = mutableListOf<Double?>()
    val finalResult = mutableListOf<Double?>()
    speedResults.forEach { result: SpeedResultRealm ->
        laneAResult.add(result.laneA?.toDoubleOrNull())
        laneBResult.add(result.laneB?.toDoubleOrNull())
        oneEightResult.add(result.oneEighth?.toDoubleOrNull())
        quarterResult.add(result.quarter?.toDoubleOrNull())
        semiResult.add(result.semiFinal?.toDoubleOrNull())
        smallResult.add(result.smallFinal?.toDoubleOrNull())
        finalResult.add(result.final?.toDoubleOrNull())
    }
    val xLabels = (1..laneAResult.size).toList()

    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val width = screenSize.getWidth()

    val chart = XYChartBuilder()
        .title("Postęp zawodnika w czasie")
        .xAxisTitle("Index")
        .yAxisTitle("Czas")
        .width(width.toInt())
        .build()

    chart.addSeries("Tor A", xLabels, laneAResult).marker = SeriesMarkers.CIRCLE
    addChartSeries(chart, "Tor B", laneBResult)
    addChartSeries(chart, "1/8", oneEightResult)
    addChartSeries(chart, "1/4", quarterResult)
    addChartSeries(chart, "Półfinał", semiResult)
    addChartSeries(chart, "Mały finał", smallResult)
    addChartSeries(chart, "Finał", finalResult)

    val image = BitmapEncoder.getBufferedImage(chart).toComposeImageBitmap()

    Column {
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
