import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.extensions.compose.jetbrains.rememberRootComponent
import com.toxicbakery.logging.Arbor
import com.toxicbakery.logging.Seedling
import io.realm.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import scraping.Scraper
import utils.navigation.Root
import utils.navigation.RootUi

fun main() {
    Arbor.sow(Seedling())
    Arbor.d("Initializing application...")

    application {
        val icon = painterResource("logo.png")
        Window(
            onCloseRequest = ::exitApplication,
            icon = icon,
            title = "IFSC Manager",
        ) {
            RootUi(root())
        }
    }
}

@Composable
private fun root(): Root =
    // The rememberRootComponent function provides the root ComponentContext and remembers the instance or Root
    rememberRootComponent { componentContext ->
        Root(
            componentContext = componentContext,
            scraper = Scraper(),
            database = Database,
            coroutineScope = CoroutineScope(Dispatchers.IO),
        )
    }
