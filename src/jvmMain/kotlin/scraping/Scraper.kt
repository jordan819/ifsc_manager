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
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebElement
import scraping.model.Climber
import java.util.NoSuchElementException
import java.util.concurrent.TimeUnit

class Scraper {

    private lateinit var driver: ChromeDriver

    init {
        setupDriver()
    }

    private fun setupDriver() {
        // Chrome version causes problems - the following driver version supports Chrome 107 only
        // Download driver from here: https://chromedriver.chromium.org/downloads
        // Check Chrome version here: chrome://settings/help
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe")
        val driverOptions = ChromeOptions()
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
                val age = (driver.findElementByClassName("age") as RemoteWebElement).findElementByTagName("strong").text.toIntOrNull()

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

    suspend fun scrape() {
        println("Starting web scraping...")
        val url =
            "https://www.ifsc-climbing.org/index.php/world-competition/calendar/?task=resultathletes&event=1235&result=6"

        // Navigate to a page
        driver.get(url)
        driver.switchTo().frame("calendar")
        val table = driver.findElementById("table_id")

        delay(500)

        val rows = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr"))

        rows.forEach { row ->
            val columns = row.findElements(By.tagName("div"))
            val rank = columns.getOrNull(0)?.text ?: ""
            val name = columns.getOrNull(1)?.text ?: ""
            val surname = columns.getOrNull(2)?.text ?: ""
            val country = columns.getOrNull(3)?.text ?: ""
            val eight = columns.getOrNull(4)?.text ?: ""
            val quarter = columns.getOrNull(5)?.text ?: ""
            val semi = columns.getOrNull(6)?.text ?: ""
            val smallFinal = columns.getOrNull(7)?.text ?: ""
            val bigFinal = columns.getOrNull(8)?.text ?: ""
            println("$rank | $name | $surname | $country | $eight | $quarter | $semi | $smallFinal | $bigFinal")
        }
    }

}
