package ui.feature.climberlist

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.realm.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import scraping.model.Climber
import scraping.model.RecordType

@Composable
fun DialogContentEditClimber(
    climberId: String,
    database: Database,
    coroutineScope: CoroutineScope,
    onConfirmButtonClicked: () -> Unit,
): @Composable () -> Unit {
    val climber = database.getClimberById(climberId) ?: return {}
    return {
        val isClimberUnofficial = climber.recordType == RecordType.UNOFFICIAL

        val name = remember { mutableStateOf(climber.name) }
        val image = remember { mutableStateOf(climber.imageUrl) }
        val date = remember { mutableStateOf(climber.dateOfBirth ?: "") }
        val country = remember { mutableStateOf(climber.country) }

        val isNameError = remember { mutableStateOf(false) }
        val dateRegex = "^\\d{4}(-(0[1-9]|1[0-2])(-(0[1-9]|[1-2][0-9]|3[0-1]))?)?\$".toRegex()
        val isDateError = remember { mutableStateOf(false) }

        fun updateClimber() = coroutineScope.launch {
            val newClimber = Climber(
                climberId = climberId,
                name = name.value.trim(),
                imageUrl = image.value,
                sex = climber.sex,
                dateOfBirth = date.value,
                country = country.value,
                federation = climber.federation,
                recordType = climber.recordType
            )
            database.updateClimber(climberId, newClimber)
            onConfirmButtonClicked()
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextField(
                enabled = isClimberUnofficial,
                singleLine = true,
                label = { Text("Imię i nazwisko") },
                value = name.value,
                modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                onValueChange = {
                    name.value = it
                    isNameError.value = name.value.isBlank()
                },
                isError = isNameError.value
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                enabled = true,
                singleLine = true,
                label = { Text("Data urodzenia") },
                value = date.value,
                modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                onValueChange = {
                    date.value = it.trim()
                    isDateError.value = !dateRegex.matches(date.value) && date.value.isNotEmpty()
                },
                isError = isDateError.value
            )

            TextField(
                enabled = isClimberUnofficial,
                singleLine = true,
                label = { Text("Kraj") },
                value = country.value,
                modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                onValueChange = { country.value = it.trim() },
            )
            TextField(
                enabled = isClimberUnofficial,
                singleLine = true,
                label = { Text("Link do zdjęcia") },
                value = image.value ?: "",
                modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                onValueChange = {
                    image.value = it
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                enabled = !isDateError.value && !isNameError.value,
                onClick = ::updateClimber,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Aktualizuj")
            }
        }
    }
}
