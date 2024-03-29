import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import ui.common.ErrorDialog
import ui.common.ErrorDisplay
import utils.navigation.Root
import utils.navigation.RootUi

fun main() {
    Arbor.sow(Seedling())
    Arbor.d("Initializing application...")

    application {
        val icon = painterResource("logo.png")

        val showError = remember { mutableStateOf(ErrorDisplay(null)) }

        Window(
            onCloseRequest = ::exitApplication,
            icon = icon,
            title = "IFSC Manager",
        ) {
            RootUi(root(showError))

            if (showError.value.message != null) {
                ErrorDialog(showError)
            }
        }
    }
}

@Composable
private fun root(errorDisplay: MutableState<ErrorDisplay>): Root =
    // The rememberRootComponent function provides the root ComponentContext and remembers the instance or Root
    rememberRootComponent { componentContext ->
        val database = Database()
        Root(
            componentContext = componentContext,
            scraper = Scraper(database),
            database = database,
            coroutineScope = CoroutineScope(Dispatchers.IO),
            errorDisplay = errorDisplay,
        )
    }
