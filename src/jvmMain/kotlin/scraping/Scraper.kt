package scraping

import com.toxicbakery.logging.Arbor
import io.realm.Database
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import scraping.model.Climber
import scraping.model.RecordType
import scraping.model.Sex
import scraping.model.boulder.BoulderGeneral
import scraping.model.common.BasicResult
import scraping.model.lead.LeadGeneral
import scraping.model.speed.SpeedFinal
import scraping.model.speed.SpeedQualification
import scraping.model.speed.SpeedResult
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Class that allows to fetch data from web page, without dedicated API.
 */
class Scraper(
    val database: Database
) {

    private val driverOptions = ChromeOptions()

    init {
        setupDriverOptions()
    }

    private fun setupDriverOptions() {
        // Download driver from here: https://chromedriver.chromium.org/downloads
        // Check Chrome version here: chrome://settings/help
        val os = System.getProperty("os.name").lowercase(Locale.getDefault())
        if (os.startsWith("windows")) {
            System.setProperty("webdriver.chrome.driver", "chromedriver.exe")
        } else if (os.startsWith("mac")) {
            System.setProperty("webdriver.chrome.driver", "chromedriver")
        }
        driverOptions.addArguments("--headless")
    }

    fun getDriverAndChromeVersions(): Pair<Int?, Int?> {
        var driverVersion: Int?
        var chromeVersion: Int?
        var driver: ChromeDriver? = null
        try {
            driver = ChromeDriver(driverOptions)
            val capabilities = driver.capabilities
            chromeVersion = capabilities.version.split(".").first().toIntOrNull()
            driverVersion =
                (capabilities.getCapability("chrome") as Map<String, String>)["chromedriverVersion"]?.split(".")
                    ?.first()?.toIntOrNull()
        } catch (e: IllegalStateException) {
            driverVersion = null
            chromeVersion = -1
        } catch (e: WebDriverException) {
            driverVersion = -1
            chromeVersion = null
        } finally {
            driver?.close()
        }
        return driverVersion to chromeVersion
    }

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    data class State(
        val log: String = ""
    )

    /**
     * Fetches data about all registered [climbers][Climber].
     *
     * Fetches information about a climber and writes it to local database.
     */
    suspend fun fetchAllClimbers() {
        Arbor.d("Fetching all climbers...")
        val url = "https://www.ifsc-climbing.org/index.php?option=com_ifsc&task=athlete.display&id="
        var climberId = 0
        val start = System.currentTimeMillis()
        val driver = ChromeDriver(driverOptions)
        loop@ while (climberId < MAX_CLIMBER_ID) {
            try {
                climberId++
                driver.get(url + climberId)
                val climber = getClimberDataFromTable(climberId, driver) ?: continue@loop
                database.writeClimber(climber)

                _state.update { uiState ->
                    uiState.copy(
                        log = "Pobieranie zawodnika z id: ${climber.climberId}..."
                    )
                }

            } catch (_: NoSuchElementException) {
                continue@loop
            }
        }
        _state.update { uiState ->
            uiState.copy(
                log = "Pobieranie zawodników zostało zakończone!"
            )
        }
        driver.close()
        val stop = System.currentTimeMillis()
        val interval = TimeUnit.MILLISECONDS.toSeconds(stop - start)
        Arbor.d("Selenium fetched ${climberId - 1} climbers in ${interval}s")
    }

    private fun getClimberDataFromTable(climberId: Int, driver: ChromeDriver): Climber? {
        val wait = WebDriverWait(driver, 30)
        try {
            wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("name"))
            )
        } catch (e: Exception) {
            Arbor.e("Climber with id $climberId could not be fetched")
            return null
        }
        val name = driver.findElementByClassName("name").text.takeUnless { it.isBlank() } ?: return null
        val country = driver.findElementByClassName("country").text
        val federation = driver.findElementByClassName("federation").text

        val sex =
            if (driver.pageSource.contains("WOMEN")) Sex.WOMAN
            else if (driver.pageSource.contains("MEN")) Sex.MAN
            else null

        val age =
            (driver.findElementByClassName("age") as RemoteWebElement).findElementByTagName("strong").text.toIntOrNull()
        val dateOfBirth = age?.let { Calendar.getInstance().get(Calendar.YEAR) - it }.toString()
        val imageUrl = "https://ifsc.results.info/athletes/$climberId/photo"
        return Climber(climberId.toString(), name, imageUrl, sex, dateOfBirth, country, federation, RecordType.OFFICIAL)
    }

    /**
     * Fetches data about all available events types, like [lead][LeadGeneral], or [speed][SpeedResult].
     *
     * Fetches information about climbers results in competitions and writes it to local database.
     * Afterwards links result with a certain [climber][Climber].
     */
    suspend fun fetchEvents() {
        Arbor.d("Fetching events...")
        _state.update { uiState ->
            uiState.copy(
                log = "Inicjowanie pobierania wyników..."
            )
        }
        val driver = ChromeDriver(driverOptions)
        val url = "https://www.ifsc-climbing.org/index.php/world-competition/calendar"

        val leaguesToFetch = listOf(
            "World Cups and World Championships",
            "IFSC Youth",
            "IFSC Europe Youth",
            "IFSC Europe Adults",
            "Games",
        )

        leaguesToFetch.forEach { leagueToFetch ->
            var currentYear: Int? = null
            yearsLoop@ do {
                driver.get(url)
                driver.switchTo().frame("calendar")
                val wait = WebDriverWait(driver, 20)
                wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.className("competition"))
                )

                val yearSelectDropdown = Select(driver.findElementById("yearSelect"))
                val leagueSelectDropdown = Select(driver.findElementById("leaguesSelect"))

                if (currentYear != null) {
                    yearSelectDropdown.selectByVisibleText((currentYear - 1).toString())
                }

                leagueSelectDropdown.selectByVisibleText(leagueToFetch)

                currentYear = yearSelectDropdown.firstSelectedOption.text.toInt()
                Arbor.d("---------------- Fetching data for year: $currentYear, league: $leagueToFetch ----------------")
                delay(500)
                try {
                    wait.until(
                        ExpectedConditions.visibilityOfElementLocated(By.className("competition"))
                    )
                } catch (e: Exception) {
                    Arbor.e("Page is empty - skipping")
                    continue@yearsLoop
                }

                val tagsWithDates = driver.findElementsByClassName("competition")
                    .map { it.findElements(By.className("tag")) to it.findElement(By.className("date")).text }

                val competitions = mutableListOf<CompetitionData>()
                tagsWithDates.forEach { tagsWithDate ->
                    tagsWithDate.first.forEach { tag ->
                        // some tags are invalid and don't have hrefs - those need to be ignored
                        val href =
                            (tag as RemoteWebElement).findElementsByTagName("a").firstOrNull()?.getAttribute("href")
                                ?: return@forEach
                        val type = tag.text.split(" ").first()
                        val dateElements = tagsWithDate.second.split(" ")
                        val date = dateElements[0] + " " + dateElements[1] + " " + dateElements.last()

                        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy").withLocale(Locale.ENGLISH)
                        val formattedDate = LocalDate.parse(date, formatter).toString()
                        competitions.add(CompetitionData(href, type, formattedDate))
                    }
                }
                val competitionsDriver = ChromeDriver(driverOptions)
                var hehe = 0
                competitions.forEach { data ->
                    if (hehe > 12) {
                        return
                    }
                    try {
                        _state.update { uiState ->
                            uiState.copy(
                                log = "Pobieranie wyników:\n" +
                                        "Liga: $leagueToFetch\n" +
                                        "Rok:$currentYear\n" +
                                        "Id wydarzenia:${data.href.split("&")[1].split("=")[1]}\n" +
                                        "Id tabeli: ${data.href.split("&")[2].split("=")[1]}"
                            )
                        }
                        delay(1500)
                        hehe++
                        Arbor.wtf(hehe.toString())
//                        fetchTableContent(
//                            url = data.href,
//                            type = data.type,
//                            date = data.date,
//                            driver = competitionsDriver
//                        )
                    } catch (e: TimeoutException) {
                        Arbor.e("Could not fetch data from ${data.href}")
                    }
                }
                competitionsDriver.close()
            } while ((currentYear ?: 0) > 2010)
        }
        driver.close()
        _state.update { uiState ->
            uiState.copy(
                log = "Pobieranie wyników zostało zakończone!"
            )
        }
        Arbor.d("All available events fetched")
    }

    private suspend fun fetchTableContent(url: String, type: String, date: String, driver: ChromeDriver) {
        driver.get(url)
        driver.switchTo().frame("calendar")

        val wait = WebDriverWait(driver, 10)
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.tagName("tr"))
        )

        var eventTitle: String
        var eventCity: String
        driver.findElementsByClassName("event_title").first().text.split("-").let {
            eventTitle = it.dropLast(1).reduce { acc, next -> "$acc $next" }
            eventCity = it.last().split(" ").dropLast(1).reduce { acc, next -> "$acc $next" }
        }

        when (type) {
            BOULDER_AND_LEAD, COMBINED -> {
                Arbor.d("$type is not supported yet - skipping $url")
                return
            }

            BOULDER -> {
                Arbor.d("Fetching $type results from $url")
                val results: MutableList<BoulderGeneral> = mutableListOf()
                val table = driver.findElementById("table_id")
                val rows = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr"))
                rows.forEach { row ->
                    val result = fetchBasicResultFromTable(row)
                    if (result.climberId != null) {
                        results.add(
                            BoulderGeneral(
                                rank = result.rank,
                                climberId = result.climberId,
                                qualification = result.qualification,
                                semiFinal = result.semiFinal,
                                final = result.final
                            )
                        )
                    }
                }
                val competitionId = generateCompetitionId(url)
                database.writeBoulderResults(results, date, competitionId, eventTitle, eventCity)
            }

            SPEED -> {
                Arbor.d("Fetching $type results from $url")
                wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.tagName("tr"))
                )
                val table = driver.findElementById("table_id")
                val generalRows = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr"))
                val ranks = mutableMapOf<Int, Int?>()
                val qualificationGenerals = mutableMapOf<Int, String?>()
                generalRows.forEach { row ->
                    val rank = row.findElements(By.className("rank")).firstOrNull()?.text?.toIntOrNull()
                    val qualificationGeneral = row.findElements(By.className("tdAlignNormal")).firstOrNull()?.text
                    val climberId =
                        row.findElement(By.tagName("a")).getAttribute("href").split("=").last().toIntOrNull() ?: return
                    ranks[climberId] = rank
                    qualificationGenerals[climberId] = qualificationGeneral
                }

                driver.findElementsByClassName("link").firstOrNull()?.click() ?: return
                wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.tagName("tr"))
                )
                val rows = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr"))
                val qualificationResults = mutableListOf<SpeedQualification>()
                val finalResults = mutableListOf<SpeedFinal>()
                rows.forEach { row ->
                    val climberId =
                        row.findElement(By.tagName("a")).getAttribute("href").split("=").last().toIntOrNull() ?: return
                    var laneA =
                        row.findElements(By.className("number")).firstOrNull()?.text.takeUnless { it.isNullOrBlank() }
                    val laneB =
                        row.findElements(By.className("number")).lastOrNull()?.text.takeUnless { it.isNullOrBlank() }
                    if (laneA == null && laneB == null) {
                        laneA = qualificationGenerals[climberId]
                    }
                    qualificationResults.add(SpeedQualification(climberId.toString(), laneA, laneB))
                }

                driver.findElementsByClassName("link").last().click()
                wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.tagName("tr"))
                )
                val rows2 = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr"))
                rows2.forEach { row ->
                    val rank = row.findElements(By.className("rank")).firstOrNull()?.text?.toIntOrNull()
                    val climberId =
                        row.findElement(By.tagName("a")).getAttribute("href").split("=").last().toIntOrNull() ?: return
                    val results = row.findElements(By.className("number"))
                    val oneEighth = results.getOrNull(0)?.text.takeUnless { it.isNullOrBlank() }
                    val quarter = results.getOrNull(1)?.text.takeUnless { it.isNullOrBlank() }
                    val semiFinal = results.getOrNull(2)?.text.takeUnless { it.isNullOrBlank() }
                    val smallFinal = results.getOrNull(3)?.text.takeUnless { it.isNullOrBlank() }
                    val final = results.getOrNull(4)?.text.takeUnless { it.isNullOrBlank() }
                    finalResults.add(
                        SpeedFinal(
                            rank,
                            climberId.toString(),
                            oneEighth,
                            quarter,
                            semiFinal,
                            smallFinal,
                            final
                        )
                    )
                }

                val results = qualificationResults.map { qualificationResult ->
                    val finalResult = finalResults.find { it.climberId == qualificationResult.climberId }
                    SpeedResult(
                        ranks[qualificationResult.climberId.toInt()],
                        qualificationResult.climberId,
                        qualificationResult.laneA,
                        qualificationResult.laneB,
                        finalResult?.oneEighth,
                        finalResult?.quarter,
                        finalResult?.semiFinal,
                        finalResult?.smallFinal,
                        finalResult?.final,
                    )
                }
                val competitionId = generateCompetitionId(url)
                database.writeSpeedResults(results, date, competitionId, eventTitle, eventCity)
            }

            LEAD -> {
                Arbor.d("Fetching $type results from $url")
                val results: MutableList<LeadGeneral> = mutableListOf()
                val table = driver.findElementById("table_id")
                val rows = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr"))
                rows.forEach { row ->
                    val result = fetchBasicResultFromTable(row)
                    if (result.climberId != null) {
                        results.add(
                            LeadGeneral(
                                rank = result.rank,
                                climberId = result.climberId,
                                qualification = result.qualification,
                                semiFinal = result.semiFinal,
                                final = result.final
                            )
                        )
                    }
                }
                val competitionId = generateCompetitionId(url)
                database.writeLeadResults(results, date, competitionId, eventTitle, eventCity)
            }

            else -> {
                Arbor.e("UNEXPECTED TYPE: $type $url")
                return
            }
        }
    }

    private fun fetchBasicResultFromTable(row: WebElement): BasicResult {
        val rank = (row as RemoteWebElement).findElementsByClassName("rank").firstOrNull()?.text?.toIntOrNull()
        val climberId = row.findElementByTagName("a").getAttribute("href").split("=").last().toIntOrNull()
        val scores = row.findElements(By.className("tdAlignNormal"))
        val qualification = scores[0].text
        val semiFinal = scores.getOrNull(1)?.text.takeUnless { it.isNullOrBlank() }
        val final = scores.getOrNull(2)?.text.takeUnless { it.isNullOrBlank() }
        return BasicResult(rank, climberId.toString(), qualification, semiFinal, final)
    }

    /**
     * Generates unique competition number, based on event id and category (male or female).
     */
    private fun generateCompetitionId(url: String) =
        url.split("&")[1].split("=")[1] + "-" + url.split("&")[2].split("=")[1]

    /**
     * Contains all types of available competitions types.
     */
    companion object {
        const val BOULDER = "BOULDER"
        const val LEAD = "LEAD"
        const val SPEED = "SPEED"
        const val BOULDER_AND_LEAD = "BOULDER&LEAD"
        const val COMBINED = "COMBINED"

        const val MAX_CLIMBER_ID = 14400
    }

}
