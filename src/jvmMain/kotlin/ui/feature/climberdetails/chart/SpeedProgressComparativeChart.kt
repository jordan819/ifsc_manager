package ui.feature.climberdetails.chart

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.realm.Database
import io.realm.model.SpeedResultRealm
import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.XYChartBuilder
import org.knowm.xchart.style.markers.SeriesMarkers
import java.awt.Toolkit

@Composable
fun SpeedProgressComparativeChart(
    climberId: String,
    database: Database,
) {

    val climberResults = database.getSpeedResultsByClimberId(climberId)
    val allResults = database.getAllSpeeds().filter { it.climberId != climberId }

    val climberLaneA: List<Double> = climberResults.mapNotNull { result: SpeedResultRealm ->
        result.laneA?.toDoubleOrNull()
    }
    val allLaneA: List<Double> = allResults.mapNotNull { result: SpeedResultRealm ->
        result.laneA?.toDoubleOrNull()
    }

    val climberLaneB: List<Double> = climberResults.mapNotNull { result: SpeedResultRealm ->
        result.laneB?.toDoubleOrNull()
    }
    val allLaneB: List<Double> = allResults.mapNotNull { result: SpeedResultRealm ->
        result.laneB?.toDoubleOrNull()
    }

    val climberEight: List<Double> = climberResults.mapNotNull { result: SpeedResultRealm ->
        result.oneEighth?.toDoubleOrNull()
    }
    val allEight: List<Double> = allResults.mapNotNull { result: SpeedResultRealm ->
        result.oneEighth?.toDoubleOrNull()
    }

    val climberQuarter: List<Double> = climberResults.mapNotNull { result: SpeedResultRealm ->
        result.quarter?.toDoubleOrNull()
    }
    val allQuarter: List<Double> = allResults.mapNotNull { result: SpeedResultRealm ->
        result.quarter?.toDoubleOrNull()
    }
    val climberSemi: List<Double> = climberResults.mapNotNull { result: SpeedResultRealm ->
        result.semiFinal?.toDoubleOrNull()
    }
    val allSemi: List<Double> = allResults.mapNotNull { result: SpeedResultRealm ->
        result.semiFinal?.toDoubleOrNull()
    }

    val climberSmall: List<Double> = climberResults.mapNotNull { result: SpeedResultRealm ->
        result.smallFinal?.toDoubleOrNull()
    }
    val allSmall: List<Double> = allResults.mapNotNull { result: SpeedResultRealm ->
        result.smallFinal?.toDoubleOrNull()
    }

    val climberFinal: List<Double> = climberResults.mapNotNull { result: SpeedResultRealm ->
        result.final?.toDoubleOrNull()
    }
    val allFinal: List<Double> = allResults.mapNotNull { result: SpeedResultRealm ->
        result.final?.toDoubleOrNull()
    }

    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val width = screenSize.getWidth()

    val chart = XYChartBuilder()
        .xAxisTitle("Index")
        .yAxisTitle("Czas")
        .width(width.toInt())
        .build()

    val climberYData = listOfNotNull(
        climberLaneA.takeUnless { it.isEmpty() }?.average(),
        climberLaneB.average(),
        climberEight.average(),
        climberQuarter.average(),
        climberSemi.average(),
        climberSmall.average(),
        climberFinal.average(),
    )
    val allYData = listOf(
        allLaneA.average(),
        allLaneB.average(),
        allEight.average(),
        allQuarter.average(),
        allSemi.average(),
        allSmall.average(),
        allFinal.average(),
    )
    val xData = (1..climberYData.size).toList()
    chart.addSeries("Zawodnik", xData, climberYData)
        .marker = SeriesMarkers.CIRCLE
    chart.addSeries("Konkurencja", allYData)

    val image = BitmapEncoder.getBufferedImage(chart).toComposeImageBitmap()

    Column {
        Text("Zawodnik na tle konkurencji")
        Image(
            bitmap = image,
            contentDescription = null,
        )
    }

}
