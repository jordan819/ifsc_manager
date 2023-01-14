package utils

import scraping.model.Climber
import scraping.model.lead.LeadGeneral
import scraping.model.speed.SpeedResult
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Class that allows to write or read from CSV file.
 */
class FileManager {

    /**
     * Writes climber to CSV file.
     *
     * @param[climber] climber to be saved
     */
    fun writeClimber(climber: Climber, pathName: String = "src/jvmMain/resources/climbers.csv") {
        if (!File(pathName).exists()) {
            File(pathName).writeText("climberId, name, age, country, federation\n")
        }
        File(pathName).appendText(
            "${climber.climberId}, ${climber.name}, ${climber.yearOfBirth}, ${climber.country}, ${climber.federation}\n"
        )
    }

    /**
     * Writes [lead results][LeadGeneral] to CSV file.
     *
     * @param[results] lead results to be saved
     * @param[year] year in which competition took place
     * @param[fileName] name of the target file
     */
    fun writeLeads(results: List<LeadGeneral>, year: String, fileName: String) {
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

    /**
     * Writes [lead results][SpeedResult] to CSV file.
     *
     * @param[results] speed results to be saved
     * @param[year] year in which competition took place
     * @param[fileName] name of the target file
     */
    fun writeSpeeds(results: List<SpeedResult>, year: String, fileName: String) {

    }

    /**
     * Reads [climber][Climber] data from CSV file
     *
     * @return list of all available climbers
     */
    fun readClimbers(): List<Climber> {
        return emptyList()
    }

    /**
     * Reads [lead][LeadGeneral] results data from CSV file
     *
     * @return list of all available lead results
     */
    fun readLeads(): List<LeadGeneral> {
        return emptyList()
    }

    /**
     * Reads [speed][SpeedResult] results data from CSV file
     *
     * @return list of all available speed results
     */
    fun readSpeeds(): List<SpeedResult> {
        return emptyList()
    }
}
