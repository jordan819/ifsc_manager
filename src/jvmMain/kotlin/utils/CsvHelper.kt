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
class CsvHelper {

    init {
        val directory = File("exported")
        if (!directory.exists()) {
            directory.mkdir()
        }
    }

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
     * Writes climbers to CSV file.
     *
     * @param[climbers] climbers list to be saved
     *
     * @return absolute path to created file
     */
    fun writeClimbers(
        climbers: List<ClimberRealm>,
        pathName: String = DEFAULT_EXPORT_DIRECTORY,
        fileName: String = DEFAULT_CLIMBERS_FILE_NAME
    ): Path {
        val fullPath = Path.of("$pathName$fileName.csv").toAbsolutePath()

        Arbor.d("Writing ${climbers.size} climbers to $fullPath")
        writer.writeAll(
            rows = climbers.map { climber ->
                listOf(
                    climber.id,
                    climber.name,
                    climber.imageUrl,
                    climber.sex,
                    climber.dateOfBirth,
                    climber.country,
                    climber.federation,
                    climber.recordType,
                )
            },
            targetFileName = fullPath.toString(),
            append = false
        )

        return fullPath
    }

    /**
     * Writes [lead results][LeadResultRealm] to CSV file.
     *
     * @param[results] lead results to be saved
     * @param[fileName] name of the target file
     */
    fun writeLeads(
        results: List<LeadResultRealm>,
        pathName: String = DEFAULT_EXPORT_DIRECTORY,
        fileName: String = DEFAULT_LEADS_FILE_PATH
    ): Path {
        val fullPath = Path.of("$pathName$fileName.csv").toAbsolutePath()
        Arbor.d("Writing ${results.size} lead results to $fullPath")

        writer.writeAll(
            rows = results.map { result ->
                listOf(
                    result.id,
                    result.date,
                    result.competitionId,
                    result.competitionTitle,
                    result.competitionCity,
                    result.rank,
                    result.climberId,
                    result.qualification,
                    result.semiFinal,
                    result.final,
                )
            },
            targetFileName = fullPath.toString(),
            append = false
        )

        return fullPath
    }

    /**
     * Writes [boulder results][BoulderResultRealm] to CSV file.
     *
     * @param[results] boulder results to be saved
     * @param[fileName] name of the target file
     */
    fun writeBoulders(
        results: List<BoulderResultRealm>,
        pathName: String = DEFAULT_EXPORT_DIRECTORY,
        fileName: String = DEFAULT_BOULDERS_FILE_PATH
    ): Path {
        val fullPath = Path.of("$pathName$fileName.csv").toAbsolutePath()
        Arbor.d("Writing ${results.size} boulder results to $fullPath")

        writer.writeAll(
            rows = results.map { result ->
                listOf(
                    result.id,
                    result.date,
                    result.competitionId,
                    result.competitionTitle,
                    result.competitionCity,
                    result.rank,
                    result.climberId,
                    result.qualification,
                    result.semiFinal,
                    result.final,
                )
            },
            targetFileName = fullPath.toString(),
            append = false
        )
        return fullPath
    }

    /**
     * Writes [lead results][SpeedResultRealm] to CSV file.
     *
     * @param[results] speed results to be saved
     * @param[fileName] name of the target file
     */
    fun writeSpeeds(
        results: List<SpeedResultRealm>,
        pathName: String = DEFAULT_EXPORT_DIRECTORY,
        fileName: String = DEFAULT_SPEED_FILE_PATH
    ): Path {
        val fullPath = Path.of("$pathName$fileName.csv").toAbsolutePath()
        Arbor.d("Writing ${results.size} speed results to $fullPath")

        writer.writeAll(
            rows = results.map { result ->
                listOf(
                    result.id,
                    result.date,
                    result.competitionId,
                    result.competitionTitle,
                    result.competitionCity,
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
            },
            targetFileName = fullPath.toString(),
            append = false
        )
        return fullPath
    }

    /**
     * Reads [climber][Climber] data from CSV file
     *
     * @return list of all available climbers
     */
    fun readClimbers(
        path: String = "$DEFAULT_EXPORT_DIRECTORY$DEFAULT_CLIMBERS_FILE_NAME.csv",
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
                    imageUrl = row[2],
                    sex = when (row[3]) {
                        Sex.MAN.name -> Sex.MAN
                        Sex.WOMAN.name -> Sex.WOMAN
                        else -> null
                    },
                    dateOfBirth = row[4],
                    country = row[5],
                    federation = row[6],
                    recordType = when (row[7]) {
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
                    date = row[1]
                    competitionId = row[2]
                    competitionTitle = row[3]
                    competitionCity = row[4]
                    rank = row[5].toIntOrNull()
                    climberId = row[6]
                    qualification = row[7]
                    semiFinal = toStringOrNull(row[8])
                    final = toStringOrNull(row[9])
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
                    date = row[1]
                    competitionId = row[2]
                    competitionTitle = row[3]
                    competitionCity = row[4]
                    rank = row[5].toIntOrNull()
                    climberId = row[6]
                    qualification = row[7]
                    semiFinal = toStringOrNull(row[8])
                    final = toStringOrNull(row[9])
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
                    date = row[1]
                    competitionId = row[2]
                    competitionTitle = row[3]
                    competitionCity = row[4]
                    rank = row[5].toIntOrNull()
                    climberId = row[6]
                    laneA = toStringOrNull(row[7])
                    laneB = toStringOrNull(row[8])
                    oneEighth = toStringOrNull(row[9])
                    quarter = toStringOrNull(row[10])
                    semiFinal = toStringOrNull(row[11])
                    smallFinal = toStringOrNull(row[12])
                    final = toStringOrNull(row[13])
                }
            )
        }
        return speedList
    }

    private fun toStringOrNull(value: String) = if (value == "null") null else value

    companion object {
        const val DEFAULT_EXPORT_DIRECTORY = "exported/"

        const val DEFAULT_CLIMBERS_FILE_NAME = "climbers"
        const val DEFAULT_LEADS_FILE_PATH = "leads"
        const val DEFAULT_BOULDERS_FILE_PATH = "boulders"
        const val DEFAULT_SPEED_FILE_PATH = "speeds"
    }
}
