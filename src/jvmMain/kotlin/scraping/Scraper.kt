package scraping

import kotlinx.coroutines.delay
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebElement
import scraping.model.Climber
import java.util.Calendar
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

    fun fetchClimbers() {
        println("Fetching all climbers...")
        val url = "https://www.ifsc-climbing.org/index.php?option=com_ifsc&task=athlete.display&id="
        var climberId = 1
        val start = Calendar.getInstance().time.time
        while (climberId < 21) {
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
                println("Succesfully fetched ${climberId-1} climbers")
                return
            }
        }
        val stop = Calendar.getInstance().time.time
        println("20/14000 climbers in ${TimeUnit.MILLISECONDS.toSeconds(stop-start)}s")
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
