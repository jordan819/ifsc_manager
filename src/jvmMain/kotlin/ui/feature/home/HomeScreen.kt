package ui.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.realm.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import scraping.Scraper

@Composable
fun HomeScreen(
    navigateToClimberList: () -> Unit,
    database: Database,
) {
//    MaterialTheme(
//        colors = Colors(
//            primary = Color(0, 139, 207, 0),
//            primaryVariant = Color(0, 139, 207, 0),
//            secondary = Color(0, 139, 207, 0),
//            secondaryVariant = Color(0, 139, 207, 0),
//            background = Color(0, 139, 207, 0),
//            surface = Color(0, 139, 207, 0),
//            error = Color(0, 139, 207, 0),
//            onPrimary = Color(0, 139, 207, 0),
//            onSecondary = Color(0, 139, 207, 0),
//            onBackground = Color(0, 139, 207, 0),
//            onSurface = Color(0, 139, 207, 0),
//            onError = Color(0, 139, 207, 0),
//            isLight = true,
//        )
//    ) {
        Row(
            Modifier.fillMaxSize(),//.background(Color(0, 139, 207, 100)),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { navigateToClimberList() },
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource("climbers.svg"),
                        contentDescription = null,
                        modifier = Modifier.height(100.dp),
                    )
                    Spacer(Modifier.height(10.dp))
                    Text("Lista zawodników")
                }
            }

            IconButton(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        Scraper(database).fetchEvents()
                    }
                },
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource("events.svg"),
                        contentDescription = null,
                        modifier = Modifier.height(100.dp),
                    )
                    Spacer(Modifier.height(10.dp))
                    Text("Pobierz eventy")
                }
            }

            IconButton(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        Scraper(database).fetchAllClimbers()
                    }
                },
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
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
//    }
}
