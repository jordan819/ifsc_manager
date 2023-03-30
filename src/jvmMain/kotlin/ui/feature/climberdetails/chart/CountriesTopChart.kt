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
fun CountriesTopChart(
    database: Database,
) {

    val results = database.getAllSpeeds()
        .filter { database.getClimberById(it.climberId)?.country != null }
        .groupBy { database.getClimberById(it.climberId)?.country }
        .mapNotNull { (country, climbers) ->
            val lowestValue = climbers.minOfOrNull { climber ->
                minOf(
                    climber.laneA?.toFloatOrNull() ?: Float.POSITIVE_INFINITY,
                    climber.laneB?.toFloatOrNull() ?: Float.POSITIVE_INFINITY,
                    climber.oneEighth?.toFloatOrNull() ?: Float.POSITIVE_INFINITY,
                    climber.quarter?.toFloatOrNull() ?: Float.POSITIVE_INFINITY,
                    climber.semiFinal?.toFloatOrNull() ?: Float.POSITIVE_INFINITY,
                    climber.smallFinal?.toFloatOrNull() ?: Float.POSITIVE_INFINITY,
                    climber.final?.toFloatOrNull() ?: Float.POSITIVE_INFINITY,
                )
            }
            if (lowestValue != null && lowestValue != Float.POSITIVE_INFINITY) {
                Pair(country, lowestValue)
            } else {
                null
            }
        }
        .sortedBy { it.second }
        .take(6)

    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val width = screenSize.getWidth()

    val chart = CategoryChartBuilder()
        .title("Rekordy krajów")
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
