package utils

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import scraping.model.Climber
import scraping.model.lead.LeadGeneral
import scraping.model.speed.SpeedResult

/**
 * Class that allows to write or read from CSV file.
 */
class FileManager {

    private val writer = csvWriter {
        delimiter = ','
        nullCode = "null"
        lineTerminator = "\n"
        outputLastLineTerminator = true
    }

    /**
     * Writes climber to CSV file.
     *
     * @param[climber] climber to be saved
     */
    fun writeClimber(climber: Climber, pathName: String = "src/jvmMain/resources/climbers.csv") {
        writer.writeAll(
            rows = listOf(
                listOf(
                    climber.climberId,
                    climber.name,
                    climber.sex,
                    climber.yearOfBirth,
                    climber.country,
                    climber.federation,
                    climber.recordType,
                )
            ),
            targetFileName = pathName,
            append = true
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
