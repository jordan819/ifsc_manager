package ui.feature.climberdetails

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
import io.realm.model.LeadResultRealm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DialogContentEditLeadResult(
    database: Database,
    coroutineScope: CoroutineScope,
    resultId: String,
    onConfirmButtonClicked: () -> Unit,
): @Composable () -> Unit {

    @Composable
    fun Speed() {
        Column {
            val result = remember { mutableStateOf(database.getLeadResultsById(resultId) ?: return) }
            val rank = remember { mutableStateOf(result.value.rank.takeUnless { it == null }?.toString() ?: "") }
            val qualification = remember { mutableStateOf(result.value.qualification) }
            val semiFinal =
                remember { mutableStateOf(result.value.semiFinal.takeUnless { it == null }?.toString() ?: "") }
            val final = remember { mutableStateOf(result.value.final.takeUnless { it == null }?.toString() ?: "") }
            val date = remember { mutableStateOf(result.value.date) }
            val competitionTitle = remember { mutableStateOf(result.value.competitionTitle) }
            val competitionCity = remember { mutableStateOf(result.value.competitionCity) }

            val dateRegex = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\$".toRegex()
            val isDateError = remember { mutableStateOf(false) }

            fun updateResult() = coroutineScope.launch {
                val updatedResult = LeadResultRealm().apply {
                    this.date = date.value.trim()
                    this.competitionTitle = competitionTitle.value.trim()
                    this.competitionCity = competitionCity.value.trim()
                    this.rank = rank.value.trim().toIntOrNull()
                    this.qualification = qualification.value
                    this.semiFinal = semiFinal.value.takeUnless { it.isBlank() }
                    this.final = final.value.takeUnless { it.isBlank() }
                }
                database.updateLeadResult(
                    resultId,
                    updatedResult,
                )
                onConfirmButtonClicked()
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextField(
                    label = { Text("Data zawodów") },
                    value = date.value,
                    modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                    onValueChange = {
                        date.value = it.trim()
                        isDateError.value = !dateRegex.matches(date.value) || date.value.isBlank()
                    },
                    isError = isDateError.value
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Nazwa zawodów") },
                    value = competitionTitle.value,
                    singleLine = true,
                    modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                    onValueChange = {
                        competitionTitle.value = it
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Miasto") },
                    value = competitionCity.value,
                    singleLine = true,
                    modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                    onValueChange = {
                        competitionCity.value = it
                    },
                )
                TextField(
                    label = { Text("Miejsce w zawodach") },
                    value = rank.value,
                    singleLine = true,
                    modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                    onValueChange = {
                        if (it.isEmpty() || it.toIntOrNull() != null) {
                            rank.value = it.trim()
                        }
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("1/8") },
                    value = qualification.value,
                    singleLine = true,
                    modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                    onValueChange = {
                        qualification.value = it.trim()
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Półfinał") },
                    value = semiFinal.value,
                    singleLine = true,
                    enabled = qualification.value.isNotBlank(),
                    modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                    onValueChange = {
                        semiFinal.value = it.trim()
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Finał") },
                    value = final.value,
                    singleLine = true,
                    enabled = semiFinal.value.isNotBlank(),
                    modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                    onValueChange = {
                        final.value = it.trim()
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = ::updateResult,
                    modifier = Modifier.align(Alignment.End),
                    enabled = !isDateError.value
                            && date.value.isNotBlank()
                            && competitionTitle.value.isNotBlank()
                            && competitionCity.value.isNotBlank()
                            && qualification.value.isNotBlank()
                ) {
                    Text(text = "Aktualizuj")
                }
            }
        }
    }

    return {
        Speed()
    }
}
