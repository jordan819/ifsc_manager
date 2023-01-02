package scraping

import com.toxicbakery.logging.Arbor
import io.realm.Database
import kotlinx.coroutines.delay
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
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
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.NoSuchElementException

/**
 * Class that allows to fetch data from web page, without dedicated API.
 */
class Scraper {

    private val driverOptions = ChromeOptions()

    init {
        setupDriverOptions()
    }

    private fun setupDriverOptions() {
        // Chrome version causes problems - the following driver version supports Chrome 107 only
        // Download driver from here: https://chromedriver.chromium.org/downloads
        // Check Chrome version here: chrome://settings/help
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe")
        driverOptions.addArguments("--headless")
    }

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

                val wait = WebDriverWait(driver, 30)
                wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.className("name"))
                )

                val name = driver.findElementByClassName("name").text.takeUnless { it.isBlank() } ?: continue@loop
                val country = driver.findElementByClassName("country").text
                val federation = driver.findElementByClassName("federation").text

                val sex =
                    if (driver.pageSource.contains("WOMEN")) Sex.WOMAN
                    else if (driver.pageSource.contains("MEN")) Sex.MAN
                    else null

                val age =
                    (driver.findElementByClassName("age") as RemoteWebElement).findElementByTagName("strong").text.toIntOrNull()
                val yearOfBirth = age?.let { Calendar.getInstance().get(Calendar.YEAR) - it }

                val climber = Climber(climberId, name, sex, yearOfBirth, country, federation, recordType = RecordType.OFFICIAL)
                Database.writeClimber(climber)
                Arbor.d(climber.toString())
            } catch (_: NoSuchElementException) {
                Arbor.d("Successfully fetched ${climberId - 1} climbers")
                continue@loop
            }
        }
        driver.close()
        val stop = System.currentTimeMillis()
        val interval = TimeUnit.MILLISECONDS.toSeconds(stop - start)
        Arbor.d("Selenium fetched ${climberId - 1} climbers in ${interval}s")
    }

    suspend fun fetchNewClimbers() {
        Arbor.d("Fetching new climbers only...")
        val oldClimbersIdList = Database.getAllClimbers().map { it.id }
        var climberId = 1
        while (climberId < MAX_CLIMBER_ID) {
            if (climberId !in oldClimbersIdList) {
                fetchSingleClimber(climberId)
            }
            climberId++
        }
        Arbor.d("Finished fetching new climbers")
    }

    private suspend fun fetchSingleClimber(climberId: Int) {
        Arbor.d("Fetching climber with id: $climberId...")
        val url = "https://www.ifsc-climbing.org/index.php?option=com_ifsc&task=athlete.display&id="
        val driver = ChromeDriver(driverOptions)
        try {
            driver.get(url + climberId)

            val wait = WebDriverWait(driver, 30)
            wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("name"))
            )

            val name = driver.findElementByClassName("name").text.takeUnless { it.isBlank() } ?: return
            val country = driver.findElementByClassName("country").text
            val federation = driver.findElementByClassName("federation").text

            val sex =
                if (driver.pageSource.contains("WOMEN")) Sex.WOMAN
                else if (driver.pageSource.contains("MEN")) Sex.MAN
                else null

            val age =
                (driver.findElementByClassName("age") as RemoteWebElement).findElementByTagName("strong").text.toIntOrNull()
            val yearOfBirth = age?.let { Calendar.getInstance().get(Calendar.YEAR) - it }

            val climber = Climber(climberId, name, sex, yearOfBirth, country, federation, recordType = RecordType.OFFICIAL)
            Database.writeClimber(climber)
            Arbor.d(climber.toString())
        } catch (_: NoSuchElementException) {
            Arbor.e("Climber with id $climberId could not be fetched")
        }
        driver.close()
    }

    /**
     * Fetches data about all available events types, like [lead][LeadGeneral], or [speed][SpeedResult].
     *
     * Fetches information about climbers results in competitions and writes it to local database.
     * Afterwards links result with a certain [climber][Climber].
     */
    suspend fun fetchEvents() {
        Arbor.d("Fetching events...")
        val driver = ChromeDriver(driverOptions)
        val url = "https://www.ifsc-climbing.org/index.php/world-competition/calendar"

        var currentYear: Int? = null

        do {
            driver.get(url)
            driver.switchTo().frame("calendar")
            val wait = WebDriverWait(driver, 30)
            wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("competition"))
            )

            val yearSelectDropdown = Select(driver.findElementById("yearSelect"))

            if (currentYear != null) {
                yearSelectDropdown.selectByVisibleText((currentYear - 1).toString())
            }

            currentYear = yearSelectDropdown.firstSelectedOption.text.toInt()
            Arbor.d("---------------- Fetching data for year $currentYear ----------------")

            delay(1000)

            wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("competition"))
            )

            val tags = driver.findElementsByClassName("tag")
            val competitions = mutableListOf<Pair<String, String>>()
            tags.forEach {
                // some tags are invalid and don't have hrefs - those need to be ignored
                val href = (it as RemoteWebElement).findElementsByTagName("a").firstOrNull()?.getAttribute("href")
                    ?: return@forEach
                val type = it.text.split(" ").first()
                competitions.add(href to type)
            }
            val competitionsDriver = ChromeDriver(driverOptions)
            competitions.forEach {
                try {
                    fetchTableContent(
                        url = it.first,
                        type = it.second,
                        currentYear = currentYear.toString(),
                        driver = competitionsDriver
                    )
                } catch (e: TimeoutException) {
                    Arbor.e("Could not fetch data from ${it.first}")
                }
            }
            competitionsDriver.close()
        } while ((currentYear ?: 0) > 2007)
        driver.close()
    }

    private suspend fun fetchTableContent(url: String, type: String, currentYear: String, driver: ChromeDriver) {
        driver.get(url)
        driver.switchTo().frame("calendar")

        val wait = WebDriverWait(driver, 10)
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.tagName("tr"))
        )

        when (type) {
            BOULDER_AND_LEAD, COMBINED -> {
                // TODO: implement
                Arbor.d("$type - skipping $url")
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
                Database.writeBoulderResults(results, currentYear.toInt(), competitionId)
            }

            SPEED -> {
                Arbor.d("Fetching $type results from $url")
                wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.tagName("tr"))
                )
                val table = driver.findElementById("table_id")
                val generalRows = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr"))
                val ranks = mutableMapOf<Int, Int?>()
                generalRows.forEach { row ->
                    val rank = row.findElements(By.className("rank")).firstOrNull()?.text?.toIntOrNull()
                    val climberId =
                        row.findElement(By.tagName("a")).getAttribute("href").split("=").last().toIntOrNull() ?: return
                    ranks[climberId] = rank
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
                    val laneA =
                        row.findElements(By.className("number")).firstOrNull()?.text.takeUnless { it.isNullOrBlank() }
                    val laneB =
                        row.findElements(By.className("number")).lastOrNull()?.text.takeUnless { it.isNullOrBlank() }
                    qualificationResults.add(SpeedQualification(climberId, laneA, laneB))
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
                    val oneEighth = results[0].text
                    val quarter = results.getOrNull(1)?.text.takeUnless { it.isNullOrBlank() }
                    val semiFinal = results.getOrNull(2)?.text.takeUnless { it.isNullOrBlank() }
                    val smallFinal = results.getOrNull(3)?.text.takeUnless { it.isNullOrBlank() }
                    val final = results.getOrNull(4)?.text.takeUnless { it.isNullOrBlank() }
                    finalResults.add(SpeedFinal(rank, climberId, oneEighth, quarter, semiFinal, smallFinal, final))
                }

                val results = qualificationResults.map { qualificationResult ->
                    val finalResult = finalResults.find { it.climberId == qualificationResult.climberId }
                    SpeedResult(
                        ranks[qualificationResult.climberId],
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
                Database.writeSpeedResults(results, currentYear.toInt(), competitionId)
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
                Database.writeLeadResults(results, currentYear.toInt(), competitionId)
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
        return BasicResult(rank, climberId, qualification, semiFinal, final)
    }

    /**
     * Generates unique competition cumber, based on event id and category (male or female).
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
