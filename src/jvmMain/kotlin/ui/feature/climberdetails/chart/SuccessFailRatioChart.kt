package ui.feature.climberdetails.chart

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.realm.Database
import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.PieChartBuilder
import java.awt.Color
import java.awt.Toolkit

@Composable
fun SuccessFailRatioPercentageChart(
    climberId: String,
    database: Database,
) {

    val speedResults = database.getSpeedResultsByClimberId(climberId)
    val leadResults = database.getLeadResultsByClimberId(climberId)
    val boulderResults = database.getBoulderResultsByClimberId(climberId)

    val keywords = listOf("fall", "false start")
    val all = speedResults.size + leadResults.size + boulderResults.size
    val failed =
        speedResults.filter {
            it.laneA in keywords
                    || it.laneB in keywords
                    || it.oneEighth in keywords
                    || it.quarter in keywords
                    || it.semiFinal in keywords
                    || it.smallFinal in keywords
                    || it.final in keywords
        }.size +
                leadResults.filter {
                    it.qualification in keywords
                            || it.semiFinal in keywords
                            || it.final in keywords
                }.size +
                boulderResults.filter {
                    it.qualification in keywords
                            || it.semiFinal in keywords
                            || it.final in keywords
                }.size


    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val width = screenSize.getWidth()

    val chart = PieChartBuilder()
        .title("Udział dyskwalifikacji we wszystkich startach")
        .width(width.toInt())
        .build()

    chart.addSeries("Dyskwalifikacje", failed)
    chart.addSeries("Pozostałe", all - failed)

    chart.styler.seriesColors = arrayOf(Color.red, Color.green)

    val image = BitmapEncoder.getBufferedImage(chart).toComposeImageBitmap()

    Column {
        Image(
            bitmap = image,
            contentDescription = null,
        )
    }

}
