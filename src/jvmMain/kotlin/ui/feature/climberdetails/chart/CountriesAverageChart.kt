package ui.feature.climberdetails.chart

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.realm.Database
import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.CategoryChartBuilder
import java.awt.Toolkit

@Composable
fun CountriesAverageChart(
    database: Database,
) {

    val results = database.getAllSpeeds()
        .filter { database.getClimberById(it.climberId)?.country != null }
        .groupBy { database.getClimberById(it.climberId)?.country }
        .mapNotNull { (country, climbers) ->
            val averageValue = climbers.flatMap { climber ->
                listOfNotNull(
                    climber.laneA?.toFloatOrNull(),
                    climber.laneB?.toFloatOrNull(),
                    climber.oneEighth?.toFloatOrNull(),
                    climber.quarter?.toFloatOrNull(),
                    climber.semiFinal?.toFloatOrNull(),
                    climber.smallFinal?.toFloatOrNull(),
                    climber.final?.toFloatOrNull(),
                )
            }.average()
            Pair(country, averageValue)
        }
        .sortedBy { it.second }
        .take(6)

    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val width = screenSize.getWidth()

    val chart = CategoryChartBuilder()
        .title("Średnie krajów")
        .width(width.toInt())
        .build()

    chart.addSeries("s", results.map { it.first }, results.map { it.second })
    chart.styler.isLegendVisible = false

    val image = BitmapEncoder.getBufferedImage(chart).toComposeImageBitmap()

    Column {
        Image(
            bitmap = image,
            contentDescription = null,
        )
    }

}
