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
import scraping.model.boulder.BoulderGeneral
import scraping.model.lead.LeadGeneral
import scraping.model.speed.SpeedResult

@Composable
fun DialogContentAddResult(
    database: Database,
    coroutineScope: CoroutineScope,
    climberId: String,
    onConfirmButtonClicked: () -> Unit,
): @Composable () -> Unit {

    val resultType = remember { mutableStateOf(ContentType.SPEED) }

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
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
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
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
                    onValueChange = {
                        competitionTitle.value = it
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Miasto") },
                    value = competitionCity.value,
                    singleLine = true,
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
                    onValueChange = {
                        competitionCity.value = it
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Miejsce w zawodach") },
                    value = rank.value,
                    singleLine = true,
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
                    onValueChange = {
                        if (it.isEmpty() || it.toIntOrNull() != null) {
                            rank.value = it.trim()
                        }
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.width(400.dp)) {
                    TextField(
                        label = { Text("Tor A") },
                        value = laneA.value,
                        singleLine = true,
                        modifier = Modifier.width(190.dp).wrapContentHeight(),
                        onValueChange = {
                            if (it.isEmpty() || it.toFloatOrNull() != null) {
                                laneA.value = it.trim()
                            }
                        },
                    )
                    TextField(
                        label = { Text("Tor B") },
                        value = laneB.value,
                        singleLine = true,
                        modifier = Modifier.width(190.dp).wrapContentHeight(),
                        onValueChange = {
                            laneB.value = it.trim()
                        },
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("1/8") },
                    value = oneEighth.value,
                    singleLine = true,
                    enabled = laneA.value.isNotBlank() || laneB.value.isNotBlank(),
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
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
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
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
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
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
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
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
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
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
            val rank = remember { mutableStateOf("") }
            val qualification = remember { mutableStateOf("") }
            val semiFinal = remember { mutableStateOf("") }
            val final = remember { mutableStateOf("") }
            val date = remember { mutableStateOf("") }
            val competitionTitle = remember { mutableStateOf("") }
            val competitionCity = remember { mutableStateOf("") }

            val dateRegex = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\$".toRegex()
            val isDateError = remember { mutableStateOf(false) }

            fun addResult() = coroutineScope.launch {
                val competitionId =
                    (database.getAllLeads().lastOrNull { it.id.contains("M") }?.id?.split("-")?.get(0)
                        ?.split("_")?.get(0)?.toInt() ?: 0) + 1
                val result = LeadGeneral(
                    rank = rank.value.trim().toIntOrNull(),
                    climberId = climberId,
                    qualification = qualification.value,
                    semiFinal = semiFinal.value.takeUnless { it.isBlank() },
                    final = final.value.takeUnless { it.isBlank() },
                )
                database.writeLeadResults(
                    listOf(result),
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
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
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
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
                    onValueChange = {
                        competitionTitle.value = it
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Miasto") },
                    value = competitionCity.value,
                    singleLine = true,
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
                    onValueChange = {
                        competitionCity.value = it
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Miejsce w zawodach") },
                    value = rank.value,
                    singleLine = true,
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
                    onValueChange = {
                        if (it.isEmpty() || it.toIntOrNull() != null) {
                            rank.value = it.trim()
                        }
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Kwalifikacje") },
                    value = qualification.value,
                    singleLine = true,
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
                    onValueChange = {
                        qualification.value = it
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Półfinał") },
                    value = semiFinal.value,
                    singleLine = true,
                    enabled = qualification.value.isNotBlank(),
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
                    onValueChange = {
                        semiFinal.value = it
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Finał") },
                    value = final.value,
                    singleLine = true,
                    enabled = semiFinal.value.isNotBlank(),
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
                    onValueChange = {
                        final.value = it
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
                            && qualification.value.isNotBlank()
                ) {
                    Text(text = "Dodaj")
                }
            }
        }
    }

    @Composable
    fun Boulder() {
        Column {
            val rank = remember { mutableStateOf("") }
            val qualification = remember { mutableStateOf("") }
            val semiFinal = remember { mutableStateOf("") }
            val final = remember { mutableStateOf("") }
            val date = remember { mutableStateOf("") }
            val competitionTitle = remember { mutableStateOf("") }
            val competitionCity = remember { mutableStateOf("") }

            val dateRegex = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\$".toRegex()
            val isDateError = remember { mutableStateOf(false) }

            fun addResult() = coroutineScope.launch {
                val competitionId =
                    (database.getAllLeads().lastOrNull { it.id.contains("M") }?.id?.split("-")?.get(0)
                        ?.split("_")?.get(0)?.toInt() ?: 0) + 1
                val result = BoulderGeneral(
                    rank = rank.value.trim().toIntOrNull(),
                    climberId = climberId,
                    qualification = qualification.value,
                    semiFinal = semiFinal.value.takeUnless { it.isBlank() },
                    final = final.value.takeUnless { it.isBlank() },
                )
                database.writeBoulderResults(
                    listOf(result),
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
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
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
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
                    onValueChange = {
                        competitionTitle.value = it
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Miasto") },
                    value = competitionCity.value,
                    singleLine = true,
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
                    onValueChange = {
                        competitionCity.value = it
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Miejsce w zawodach") },
                    value = rank.value,
                    singleLine = true,
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
                    onValueChange = {
                        if (it.isEmpty() || it.toIntOrNull() != null) {
                            rank.value = it.trim()
                        }
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Kwalifikacje") },
                    value = qualification.value,
                    singleLine = true,
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
                    onValueChange = {
                        qualification.value = it
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Półfinał") },
                    value = semiFinal.value,
                    singleLine = true,
                    enabled = qualification.value.isNotBlank(),
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
                    onValueChange = {
                        semiFinal.value = it
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    label = { Text("Finał") },
                    value = final.value,
                    singleLine = true,
                    enabled = semiFinal.value.isNotBlank(),
                    modifier = Modifier.weight(1F).width(400.dp).wrapContentHeight(),
                    onValueChange = {
                        final.value = it
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
                            && qualification.value.isNotBlank()
                ) {
                    Text(text = "Dodaj")
                }
            }
        }
    }

    return {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.wrapContentHeight()
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
                    Text("Prowadzenie")
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
                    Text("Bouldering")
                }
            }
            Spacer(Modifier.height(10.dp))
            when (resultType.value) {
                ContentType.SPEED -> Speed()
                ContentType.LEAD -> Lead()
                ContentType.BOULDER -> Boulder()
            }

        }
    }

}
