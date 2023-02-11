package ui.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.realm.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import scraping.Scraper
import utils.AppColors

@Composable
fun HomeScreen(
    navigateToClimberList: () -> Unit,
    database: Database,
) {
    Row(
        Modifier.fillMaxSize().background(AppColors.Gray),
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
                    CoroutineScope(Dispatchers.IO).launch {
                        Scraper(database).fetchEvents()
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
                    CoroutineScope(Dispatchers.IO).launch {
                        Scraper(database).fetchAllClimbers()
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
}
