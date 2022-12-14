package utils.navigation

import com.arkivanov.decompose.statekeeper.Parcelable

sealed class Configuration : Parcelable {
    object Home : Configuration()
    object ClimberList : Configuration()
    data class ClimberDetails(val climberId: String) : Configuration()
}
