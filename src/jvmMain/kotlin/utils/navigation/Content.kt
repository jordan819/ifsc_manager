package utils.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.pop
import com.arkivanov.decompose.push
import com.arkivanov.decompose.router
import io.realm.Database
import kotlinx.coroutines.CoroutineScope
import scraping.Scraper
import ui.feature.climberlist.ClimberList
import ui.feature.climberlist.ClimberListUi
import ui.feature.home.Home
import ui.feature.home.HomeUi

typealias Content = @Composable () -> Unit

fun <T : Any> T.asContent(content: @Composable (T) -> Unit): Content = { content(this) }

class Root(
    componentContext: ComponentContext, // In Decompose each component has its own ComponentContext
    private val scraper: Scraper,
    private val database: Database,
    private val coroutineScope: CoroutineScope,
) : ComponentContext by componentContext {

    private val router =
        router<Configuration, Content>(
            initialConfiguration = Configuration.Home, // Starting with Home
            childFactory = ::createChild // The Router calls this function, providing the child Configuration and ComponentContext
        )

    val routerState = router.state

    private fun createChild(configuration: Configuration, context: ComponentContext): Content =
        when (configuration) {
            Configuration.Home -> home()
            Configuration.ClimberList -> climberList()
        }

    private fun home(): Content =
        Home(
            navigateToClimberList = { router.push(Configuration.ClimberList) }
        ).asContent { HomeUi(it) }

    private fun climberList(): Content =
        ClimberList(
            scraper = scraper,
            database = database,
            onBackClick = router::pop
        ).asContent { ClimberListUi(it) }

}

@Composable
fun RootUi(root: Root) {
    Children(root.routerState) { child ->
        child.instance()
    }
}
