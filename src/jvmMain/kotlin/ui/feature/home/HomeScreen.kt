package ui.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.realm.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import scraping.Scraper
import ui.common.ErrorDisplay
import utils.AppColors

@Composable
fun HomeScreen(
    navigateToClimberList: () -> Unit,
    database: Database,
    errorDisplay: MutableState<ErrorDisplay>,
    coroutineScope: CoroutineScope,
) {

    val logs = remember { mutableStateOf<String?>(null) }

    var scraper: Scraper

    fun isDriverCompatible(): Boolean {
        scraper = Scraper(database)
        val (driverVersion, chromeVersion) = scraper.getDriverAndChromeVersions()
        if (driverVersion == null) {
            val message: AnnotatedString = buildAnnotatedString {
                val str =
                    "Nie znaleziono sterownika!\n\nPobierz sterownik i umieść go w folderze z aplikacją."
                val startIndex = str.indexOf("Pobierz")
                val endIndex = startIndex + 7
                append(str)
                addStyle(
                    style = SpanStyle(
                        color = Color(0xff64B5F6),
                        textDecoration = TextDecoration.Underline
                    ),
                    start = startIndex,
                    end = endIndex
                )
                addStringAnnotation(
                    tag = "URL",
                    annotation = "https://chromedriver.chromium.org/downloads",
                    start = startIndex,
                    end = endIndex
                )
            }
            errorDisplay.value = ErrorDisplay(message)
            return false
        }
        if (chromeVersion == null) {
            val message: AnnotatedString = buildAnnotatedString {
                val str = "Nie znaleziono przeglądarki Google Chrome.\nZainstaluj ją i spróbuj ponownie."
                append(str)
            }
            errorDisplay.value = ErrorDisplay(message)
            return false
        }
        if (driverVersion != chromeVersion) {
            val message: AnnotatedString = buildAnnotatedString {
                val str = "Niezgodna wersja sterownika!\n\n" +
                        "Pobierz sterownik dla wersji Chrome $chromeVersion i umieść go w folderze z aplikacją."
                val startIndex = str.indexOf("Pobierz")
                val endIndex = startIndex + 7
                append(str)
                addStyle(
                    style = SpanStyle(
                        color = Color(0xff64B5F6),
                        textDecoration = TextDecoration.Underline
                    ),
                    start = startIndex,
                    end = endIndex
                )
                addStringAnnotation(
                    tag = "URL",
                    annotation = "https://chromedriver.chromium.org/downloads",
                    start = startIndex,
                    end = endIndex
                )
            }
            errorDisplay.value = ErrorDisplay(message)
            return false
        }
        return true
    }

    suspend fun fetchClimbers() {
        if (isDriverCompatible()) {
            scraper = Scraper(database)
            scraper.state.onEach { list ->
                logs.value = list.log
            }
                .launchIn(CoroutineScope(Dispatchers.IO))
            scraper.fetchAllClimbers()
        }
    }

    suspend fun fetchEvents() {
        if (isDriverCompatible()) {
            scraper = Scraper(database)
            scraper.state.onEach { list ->
                logs.value = list.log
            }
                .launchIn(CoroutineScope(Dispatchers.IO))
            scraper.fetchEvents()
        }
    }

    Column(
        Modifier.fillMaxSize().background(AppColors.Gray),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .height(180.dp)
                    .width(180.dp)
                    .clickable { navigateToClimberList() }
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = painterResource("climbers.svg"),
                    contentDescription = null,
                    modifier = Modifier.height(100.dp),
                )
                Spacer(Modifier.height(10.dp))
                Text("Lista zawodników")
            }

            Column(
                modifier = Modifier
                    .height(180.dp)
                    .width(180.dp)
                    .clickable {
                        coroutineScope.launch {
                            fetchEvents()
                        }
                    }
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = painterResource("events.svg"),
                    contentDescription = null,
                    modifier = Modifier.height(100.dp),
                )
                Spacer(Modifier.height(10.dp))
                Text("Pobierz wydarzenia")
            }

            Column(
                modifier = Modifier
                    .height(180.dp)
                    .width(180.dp)
                    .clickable {
                        coroutineScope.launch {
                            fetchClimbers()
                        }
                    }
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = painterResource("climbers-fetch.svg"),
                    contentDescription = null,
                    modifier = Modifier.height(100.dp),
                )
                Spacer(Modifier.height(10.dp))
                Text("Pobierz zawodników")
            }
        }

        val log = logs.value
        if (log != null) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = log,
                    fontSize = 24.sp,
                )

            }
        }
    }
}
