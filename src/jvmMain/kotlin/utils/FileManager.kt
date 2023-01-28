package utils

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.toxicbakery.logging.Arbor
import io.realm.model.BoulderResultRealm
import io.realm.model.ClimberRealm
import io.realm.model.LeadResultRealm
import io.realm.model.SpeedResultRealm
import scraping.model.Climber
import scraping.model.RecordType
import scraping.model.Sex
import java.io.File
import java.nio.file.Path

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
     *
     * @return absolute path to created file
     */
    fun writeClimber(
        climber: ClimberRealm,
        pathName: String = DEFAULT_CLIMBERS_FILE_PATH,
        fileName: String = DEFAULT_CLIMBERS_FILE_NAME
    ): Path {
        val fullPath = "$pathName$fileName.csv"

        Arbor.d("Writing climber with id: ${climber.id} to $fullPath")
        writer.writeAll(
            rows = listOf(
                listOf(
                    climber.id,
                    climber.name,
                    climber.sex,
                    climber.yearOfBirth,
                    climber.country,
                    climber.federation,
                    climber.recordType,
                )
            ),
            targetFileName = fullPath,
            append = true
        )

        return Path.of(fullPath).toAbsolutePath()
    }

    /**
     * Writes [lead results][LeadResultRealm] to CSV file.
     *
     * @param[results] lead results to be saved
     * @param[fileName] name of the target file
     */
    fun writeLeads(results: List<LeadResultRealm>, fileName: String = DEFAULT_LEADS_FILE_PATH) {
        results.forEach { result ->
            writer.writeAll(
                rows = listOf(
                    listOf(
                        result.id,
                        result.year,
                        result.competitionId,
                        result.rank,
                        result.climberId,
                        result.qualification,
                        result.semiFinal,
                        result.final,
                    )
                ),
                targetFileName = fileName,
                append = true
            )
        }
    }

    /**
     * Writes [boulder results][BoulderResultRealm] to CSV file.
     *
     * @param[results] boulder results to be saved
     * @param[fileName] name of the target file
     */
    fun writeBoulders(results: List<BoulderResultRealm>, fileName: String = DEFAULT_BOULDERS_FILE_PATH) {
        results.forEach { result ->
            writer.writeAll(
                rows = listOf(
                    listOf(
                        result.id,
                        result.year,
                        result.competitionId,
                        result.rank,
                        result.climberId,
                        result.qualification,
                        result.semiFinal,
                        result.final,
                    )
                ),
                targetFileName = fileName,
                append = true
            )
        }
    }

    /**
     * Writes [lead results][SpeedResultRealm] to CSV file.
     *
     * @param[results] speed results to be saved
     * @param[fileName] name of the target file
     */
    fun writeSpeeds(results: List<SpeedResultRealm>, fileName: String = DEFAULT_SPEED_FILE_PATH) {
        results.forEach { result ->
            writer.writeAll(
                rows = listOf(
                    listOf(
                        result.id,
                        result.year,
                        result.rank,
                        result.climberId,
                        result.laneA,
                        result.laneB,
                        result.oneEighth,
                        result.quarter,
                        result.semiFinal,
                        result.smallFinal,
                        result.final,
                    )
                ),
                targetFileName = fileName,
                append = true
            )
        }
    }

    /**
     * Reads [climber][Climber] data from CSV file
     *
     * @return list of all available climbers
     */
    fun readClimbers(
        path: String = "$DEFAULT_CLIMBERS_FILE_PATH$DEFAULT_CLIMBERS_FILE_NAME.csv",
    ): List<Climber> {
        Arbor.d("Reading climbers from $path")

        val file = File(path)
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
     * Reads [lead][LeadResultRealm] results data from CSV file
     *
     * @return list of all available lead results
     */
    fun readLeads(pathName: String = DEFAULT_LEADS_FILE_PATH): List<LeadResultRealm> {
        val file = File(pathName)
        val rows = reader.readAll(file)

        val leadList = mutableListOf<LeadResultRealm>()

        rows.forEach { row ->
            leadList.add(
                LeadResultRealm().apply {
                    id = row[0]
                    year = row[1].toInt()
                    competitionId = row[2]
                    rank = row[3].toIntOrNull()
                    climberId = row[4]
                    qualification = row[5]
                    semiFinal = row[6]
                    final = row[7]
                }
            )
        }
        return leadList
    }

    /**
     * Reads [boulder][BoulderResultRealm] results data from CSV file
     *
     * @return list of all available lead results
     */
    fun readBoulders(pathName: String = DEFAULT_BOULDERS_FILE_PATH): List<BoulderResultRealm> {
        val file = File(pathName)
        val rows = reader.readAll(file)

        val boulderList = mutableListOf<BoulderResultRealm>()

        rows.forEach { row ->
            boulderList.add(
                BoulderResultRealm().apply {
                    id = row[0]
                    year = row[1].toInt()
                    competitionId = row[2]
                    rank = row[3].toIntOrNull()
                    climberId = row[4]
                    qualification = row[5]
                    semiFinal = row[6]
                    final = row[7]
                }
            )
        }
        return boulderList
    }

    /**
     * Reads [speed][SpeedResultRealm] results data from CSV file
     *
     * @return list of all available speed results
     */
    fun readSpeeds(pathName: String = DEFAULT_SPEED_FILE_PATH): List<SpeedResultRealm> {
        val file = File(pathName)
        val rows = reader.readAll(file)

        val speedList = mutableListOf<SpeedResultRealm>()

        rows.forEach { row ->
            speedList.add(
                SpeedResultRealm().apply {
                    id = row[0]
                    year = row[1].toInt()
                    rank = row[2].toIntOrNull()
                    climberId = row[3]
                    laneA = row[4]
                    laneB = row[5]
                    oneEighth = row[6]
                    quarter = row[7]
                    semiFinal = row[8]
                    smallFinal = row[9]
                    final = row[10]
                }
            )
        }
        return speedList
    }

    companion object {
        const val DEFAULT_CLIMBERS_FILE_NAME = "climbers"
        const val DEFAULT_CLIMBERS_FILE_PATH = "exported/"

        const val DEFAULT_LEADS_FILE_PATH = "src/jvmMain/resources/leads.csv"
        const val DEFAULT_BOULDERS_FILE_PATH = "src/jvmMain/resources/boulders.csv"
        const val DEFAULT_SPEED_FILE_PATH = "src/jvmMain/resources/speeds.csv"
    }
}
