package ui.feature.climberlist

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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
        val date = remember { mutableStateOf(climber.dateOfBirth ?: "") }
        val country = remember { mutableStateOf(climber.country) }

        fun updateClimber() = coroutineScope.launch {
            val newClimber = Climber(
                climberId = climberId,
                name = name.value,
                sex = climber.sex,
                dateOfBirth = date.value,
                country = country.value,
                federation = climber.federation,
                recordType = climber.recordType
            )
            database.updateClimber(climberId, newClimber)
            onConfirmButtonClicked()
        }

        MaterialTheme {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextField(
                    enabled = isClimberUnofficial,
                    label = { Text("Imię i nazwisko") },
                    value = name.value,
                    modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                    onValueChange = { name.value = it },
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    enabled = true,
                    label = { Text("Data urodzenia") },
                    value = date.value,
                    modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                    onValueChange = { date.value = it },
                )

                TextField(
                    enabled = isClimberUnofficial,
                    label = { Text("Kraj") },
                    value = country.value,
                    modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                    onValueChange = { country.value = it },
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = ::updateClimber,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "Aktualizuj")
                }

            }
        }

    }
}
