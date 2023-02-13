package ui.feature.climberdetails

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
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
import scraping.model.speed.SpeedResult

@Composable
fun DialogContentAddResult(
    database: Database,
    coroutineScope: CoroutineScope,
    climberId: String,
    defaultResultType: String?,
    onConfirmButtonClicked: () -> Unit,
): @Composable () -> Unit {

    val resultType = remember { mutableStateOf(defaultResultType) }

    @Composable
    fun Speed() {
        Column {
            val rank = remember { mutableStateOf("") }
            val laneA = remember { mutableStateOf("") }
            val laneB = remember { mutableStateOf("") }
            val oneEighth = remember { mutableStateOf("") }
            val quarter = remember { mutableStateOf("") }
            val semiFinal = remember { mutableStateOf("") }
            val smallFinal = remember { mutableStateOf("") }
            val final = remember { mutableStateOf("") }
            val date = remember { mutableStateOf("") }
            val competitionTitle = remember { mutableStateOf("") }
            val competitionCity = remember { mutableStateOf("") }

            val dateRegex = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\$".toRegex()
            val isDateError = remember { mutableStateOf(false) }

            fun addResult() = coroutineScope.launch {
                val competitionId =
                    (database.getAllSpeeds().lastOrNull { it.id.contains("M") }?.id?.split("-")?.get(0)
                        ?.split("_")?.get(0)?.toInt() ?: 0) + 1
                val result = SpeedResult(
                    rank = rank.value.trim().toIntOrNull(),
                    climberId = climberId,
                    laneA = laneA.value.takeUnless { it.isBlank() },
                    laneB = laneB.value.takeUnless { it.isBlank() },
                    oneEighth = oneEighth.value.takeUnless { it.isBlank() },
                    quarter = quarter.value.takeUnless { it.isBlank() },
                    semiFinal = semiFinal.value.takeUnless { it.isBlank() },
                    smallFinal = smallFinal.value.takeUnless { it.isBlank() },
                    final = final.value.takeUnless { it.isBlank() },
                )
                database.writeSpeedResult(
                    result,
                    date.value,
                    competitionId.toString(),
                    competitionTitle.value,
                    competitionCity.value
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
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Tor A") },
                    value = laneA.value,
                    singleLine = true,
                    modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                    onValueChange = {
                        if (it.isEmpty() || it.toFloatOrNull() != null) {
                            laneA.value = it.trim()
                        }
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Tor B") },
                    value = laneB.value,
                    singleLine = true,
                    modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                    onValueChange = {
                        laneB.value = it.trim()
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("1/8") },
                    value = oneEighth.value,
                    singleLine = true,
                    enabled = laneA.value.isNotBlank() || laneB.value.isNotBlank(),
                    modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                    onValueChange = {
                        oneEighth.value = it.trim()
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Ćwierćfinał") },
                    value = quarter.value,
                    singleLine = true,
                    enabled = oneEighth.value.isNotBlank(),
                    modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                    onValueChange = {
                        quarter.value = it.trim()
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Półfinał") },
                    value = semiFinal.value,
                    singleLine = true,
                    enabled = quarter.value.isNotBlank(),
                    modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                    onValueChange = {
                        semiFinal.value = it.trim()
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Mały Finał") },
                    value = smallFinal.value,
                    singleLine = true,
                    enabled = semiFinal.value.isNotBlank() && final.value.isBlank(),
                    modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                    onValueChange = {
                        smallFinal.value = it.trim()
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Finał") },
                    value = final.value,
                    singleLine = true,
                    enabled = semiFinal.value.isNotBlank() && smallFinal.value.isBlank(),
                    modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                    onValueChange = {
                        final.value = it.trim()
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = ::addResult,
                    modifier = Modifier.align(Alignment.End),
                    enabled = !isDateError.value
                            && date.value.isNotBlank()
                            && competitionTitle.value.isNotBlank()
                            && competitionCity.value.isNotBlank()
                            && (laneA.value.isNotBlank() || laneB.value.isNotBlank())
                ) {
                    Text(text = "Dodaj")
                }
            }
        }
    }

    @Composable
    fun Lead() {
        Column {
            Text("Lead")
        }
    }

    @Composable
    fun Boulder() {
        Column {
            Text("Boulder")
        }
    }

    return {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.width(400.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RadioButton(
                        selected = (resultType.value == ContentType.SPEED),
                        onClick = {
                            resultType.value = ContentType.SPEED
                        },
                    )
                    Text("Czas")
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RadioButton(
                        selected = (resultType.value == ContentType.LEAD),
                        onClick = {
                            resultType.value = ContentType.LEAD
                        },
                    )
                    Text("Bouldering")
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RadioButton(
                        selected = (resultType.value == ContentType.BOULDER),
                        onClick = {
                            resultType.value = ContentType.BOULDER
                        },
                    )
                    Text("Prowadzenie")
                }
            }
            Spacer(Modifier.height(10.dp))
            when (resultType.value) {
                ContentType.SPEED -> Speed()
                ContentType.LEAD -> Lead()
                ContentType.BOULDER -> Boulder()
                else -> Speed()
            }

        }
    }

}
