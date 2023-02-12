package ui.feature.climberlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
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
import scraping.model.Sex

@Composable
fun DialogContentAddClimber(
    database: Database,
    coroutineScope: CoroutineScope,
    onConfirmButtonClicked: () -> Unit,
): @Composable () -> Unit {

    return {

        val name = remember { mutableStateOf("") }
        val image = remember { mutableStateOf("") }
        val date = remember { mutableStateOf("") }
        val country = remember { mutableStateOf("") }

        val isNameError = remember { mutableStateOf(false) }
        val dateRegex = "^\\d{4}(-(0[1-9]|1[0-2])(-(0[1-9]|[1-2][0-9]|3[0-1]))?)?\$".toRegex()
        val isDateError = remember { mutableStateOf(false) }
        val isCountryError = remember { mutableStateOf(false) }
        val isButtonEnabled = remember { mutableStateOf(false) }

        val radioOptions = listOf("Mężczyzna", "Kobieta")
        val selectedOption = remember { mutableStateOf<String?>(null) }

        fun updateButtonState() {
            isButtonEnabled.value =
                name.value.isNotBlank() &&
                        country.value.isNotBlank() &&
                        !isNameError.value &&
                        !isDateError.value &&
                        !isCountryError.value &&
                        selectedOption.value != null
        }

        fun addClimber() = coroutineScope.launch {
            val id =
                (database.getAllClimbers().lastOrNull { it.id.contains("M") }?.id?.split("-")?.get(0)?.toInt() ?: 0) + 1
            val climber = Climber(
                climberId = "${id}-M",
                name = name.value.trim(),
                imageUrl = image.value,
                sex = when (selectedOption.value) {
                    "Mężczyzna" -> Sex.MAN
                    "Kobieta" -> Sex.WOMAN
                    else -> null
                },
                dateOfBirth = date.value.takeUnless { it.isBlank() },
                country = country.value.trim(),
                recordType = RecordType.UNOFFICIAL
            )
            database.writeClimber(climber)
            onConfirmButtonClicked()
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                label = { Text("Imię i nazwisko") },
                value = name.value,
                singleLine = true,
                modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                onValueChange = {
                    name.value = it
                    isNameError.value = name.value.isBlank()
                    updateButtonState()
                },
                isError = isNameError.value
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                radioOptions.forEach { text ->
                    Row(
                        Modifier
                            .width(IntrinsicSize.Min)
                            .height(40.dp)
                            .selectable(
                                selected = (text == selectedOption.value),
                                onClick = {
                                    selectedOption.value = text
                                    updateButtonState()
                                }
                            )
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            RadioButton(
                                selected = (text == selectedOption.value),
                                onClick = {
                                    selectedOption.value = text
                                    updateButtonState()
                                },
                            )
                        }
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.body1.merge(),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }

            TextField(
                label = { Text("Data urodzenia") },
                value = date.value,
                modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                onValueChange = {
                    date.value = it.trim()
                    isDateError.value = !dateRegex.matches(date.value) && date.value.isNotEmpty()
                    updateButtonState()
                },
                isError = isDateError.value
            )

            TextField(
                label = { Text("Kraj") },
                value = country.value,
                modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                onValueChange = {
                    country.value = it.trim()
                    isNameError.value = name.value.isBlank()
                    updateButtonState()
                },
            )
            TextField(
                label = { Text("Link do zdjęcia") },
                value = image.value,
                singleLine = true,
                modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                onValueChange = {
                    image.value = it
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = ::addClimber,
                modifier = Modifier.align(Alignment.End),
                enabled = isButtonEnabled.value,
            ) {
                Text(text = "Dodaj")
            }
        }
    }
}

