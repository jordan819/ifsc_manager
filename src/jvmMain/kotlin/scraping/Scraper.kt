package scraping

import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.div
import it.skrape.selects.html5.h1
import it.skrape.selects.html5.span
import it.skrape.selects.html5.strong
import kotlinx.coroutines.delay
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import scraping.model.Climber
import scraping.model.lead.LeadGeneral
import utils.FileManager
import java.util.concurrent.TimeUnit

class Scraper {

    private lateinit var driver: ChromeDriver
    private val driverOptions = ChromeOptions()

    init {
        setupDriver()
    }

    private fun setupDriver() {
        // Chrome version causes problems - the following driver version supports Chrome 107 only
        // Download driver from here: https://chromedriver.chromium.org/downloads
        // Check Chrome version here: chrome://settings/help
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe")
        driverOptions.addArguments("--headless")
        driver = ChromeDriver(driverOptions)
    }

    /**
     * SkrapeIt is way less efficient than expected. Stays in code for now, but shouldn't be used.
     */
    fun fetchClimbersWithSkrapeIt() {
        var climberId = 1
        println("Fetching all climbers using SkrapeIt...")
        val start = System.currentTimeMillis()
        while (climberId < 101) {
            skrape(BrowserFetcher) {
                request {
                    url = "https://www.ifsc-climbing.org/index.php?option=com_ifsc&task=athlete.display&id=$climberId"
                }
                extractIt<Climber> {
                    htmlDocument {
                        relaxed = true

                        it.climberId = climberId

                        h1 {
                            withClass = "name"
                            findFirst {
                                it.name = this.text
                            }
                        }
                        div {
                            withClass = "country"
                            findFirst {
                                span {
                                    it.country = this.findFirst { this.text }
                                }
                            }
                        }
                        span {
                            withClass = "federation"
                            findFirst {
                                it.federation = this.text
                            }
                        }
                        span {
                            withClass = "age"
                            findFirst {
                                strong {
                                    findFirst {
                                        it.age = this.text.toIntOrNull()
                                    }
                                }
                            }
                        }
                        println(it)
                    }
                }
            }
            climberId++
        }
        val stop = System.currentTimeMillis()
        val interval = TimeUnit.MILLISECONDS.toSeconds(stop - start)
        println("SkrapeIt fetched ${climberId - 1} climbers in ${interval}s")
    }


    fun fetchClimbersWithSelenium() {
        println("Fetching all climbers using Selenium...")
        val url = "https://www.ifsc-climbing.org/index.php?option=com_ifsc&task=athlete.display&id="
        var climberId = 1
        val start = System.currentTimeMillis()
        while (climberId < 101) {
            try {
                driver.get(url + climberId)
                val name = driver.findElementByClassName("name").text
                val country = driver.findElementByClassName("country").text
                val federation = driver.findElementByClassName("federation").text
                val age =
                    (driver.findElementByClassName("age") as RemoteWebElement).findElementByTagName("strong").text.toIntOrNull()

                val climber = Climber(climberId, name, age, country, federation)
                println(climber)

                climberId++
            } catch (_: NoSuchElementException) {
                println("Succesfully fetched ${climberId - 1} climbers")
                return
            }
        }
        val stop = System.currentTimeMillis()
        val interval = TimeUnit.MILLISECONDS.toSeconds(stop - start)
        println("Selenium fetched ${climberId - 1} climbers in ${interval}s")
    }

    suspend fun fetchEvents() {
        println("Fetching events...")
        val url = "https://www.ifsc-climbing.org/index.php/world-competition/calendar"

        var currentYear: Int? = null

        while (true) {
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
            println("---------------- Fetching data for year $currentYear ----------------")

            //TODO: maybe sth smarter can be done
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
                    System.err.println("Could not fetch data from ${it.first}")
                }
            }
            competitionsDriver.close()
        }
    }

    private fun fetchTableContent(url: String, type: String, currentYear: String, driver: ChromeDriver) {
        driver.get(url)
        driver.switchTo().frame("calendar")

        val wait = WebDriverWait(driver, 10)
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.tagName("tr"))
        )

        val table = driver.findElementById("table_id")

        val rows = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr"))

        when (type) {
            BOULDER, SPEED, BOULDER_AND_LEAD, COMBINED -> {
                // TODO: implement
                println("$type - skipping $url")
                return
            }
            LEAD -> {
                println("Fetching $type results from $url")
                val results: MutableList<LeadGeneral> = mutableListOf()
                rows.forEach { row ->
                    val rank =
                        (row as RemoteWebElement).findElementsByClassName("rank").firstOrNull()?.text?.toIntOrNull()
                    val climberId =
                        row.findElementByTagName("a").getAttribute("href").split("=").last().toIntOrNull() ?: return
                    val scores = row.findElements(By.className("tdAlignNormal"))
                    val qualification = scores[0].text
                    val semiFinal = scores.getOrNull(1)?.text
                    val final = scores.getOrNull(2)?.text
                    val result = LeadGeneral(rank, climberId, qualification, semiFinal, final)
                    results.add(result)
                }
                val fileName = url.split("&")[1] + "_" + url.split("&")[2]
                FileManager().writeToCsv(results, currentYear, fileName)
            }
            else -> {
                System.err.println("UNEXPECTED TYPE: $type $url")
                return
            }
        }
    }

    companion object {
        const val BOULDER = "BOULDER"
        const val LEAD = "LEAD"
        const val SPEED = "SPEED"
        const val BOULDER_AND_LEAD = "BOULDER&LEAD"
        const val COMBINED = "COMBINED"
    }

}
