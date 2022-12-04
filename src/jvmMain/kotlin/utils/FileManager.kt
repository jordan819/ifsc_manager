package utils

import scraping.model.Climber
import scraping.model.lead.LeadGeneral
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class FileManager {

    fun writeToCsv(climber: Climber) {
        val pathName = "src/jvmMain/resources/climbers.csv"
        if (!File(pathName).exists()) {
            File(pathName).writeText("climberId, name, age, country, federation\n")
        }
        File("src/jvmMain/resources/climbers").appendText(
            "${climber.climberId}, ${climber.name}, ${climber.age}, ${climber.country}, ${climber.federation}\n"
        )
    }

    fun writeToCsv(results: List<LeadGeneral>, year: String, fileName: String) {
        val directory = "src/jvmMain/resources/results/lead/$year"
        Files.createDirectories(Paths.get(directory))
        val filePath = "$directory/$fileName.csv"
        if (!File(filePath).exists()) {
            File(filePath).writeText("rank, climberId, qualification, semiFinal, final\n")
        }
        results.forEach { result ->
            File(filePath).appendText(
                "${result.rank}, ${result.climberId}, ${result.qualification}, ${result.semiFinal}, ${result.final}\n"
            )
        }

    }
}
