package utils

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import io.realm.model.LeadResultRealm
import scraping.model.Climber
import scraping.model.RecordType
import scraping.model.Sex
import scraping.model.lead.LeadGeneral
import scraping.model.speed.SpeedResult
import java.io.File

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

    private val reader = csvReader {
        delimiter = ','
    }

    /**
     * Writes climber to CSV file.
     *
     * @param[climber] climber to be saved
     */
    fun writeClimber(climber: Climber, pathName: String = DEFAULT_CLIMBERS_FILE_PATH) {
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
     * @param[fileName] name of the target file
     */
    fun writeLeads(results: List<LeadResultRealm>, fileName: String) {

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
    fun readClimbers(pathName: String = DEFAULT_CLIMBERS_FILE_PATH): List<Climber> {
        val file = File(pathName)
        val rows = reader.readAll(file)

        val climberList = mutableListOf<Climber>()

        rows.forEach { row ->
            climberList.add(
                Climber(
                    climberId = row[0],
                    name = row[1],
                    sex = when (row[2]) {
                        Sex.MAN.name -> Sex.MAN
                        Sex.WOMAN.name -> Sex.WOMAN
                        else -> null
                    },
                    yearOfBirth = row[3].toIntOrNull(),
                    country = row[4],
                    federation = row[5],
                    recordType = when (row[6]) {
                        RecordType.OFFICIAL.name -> RecordType.OFFICIAL
                        RecordType.UNOFFICIAL.name -> RecordType.UNOFFICIAL
                        else -> RecordType.UNOFFICIAL
                    },
                )
            )
        }
        return climberList
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

    companion object {
        const val DEFAULT_CLIMBERS_FILE_PATH = "src/jvmMain/resources/climbers.csv"
    }
}
