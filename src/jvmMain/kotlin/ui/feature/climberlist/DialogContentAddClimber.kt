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
        val birthYear = remember { mutableStateOf("") }
        val country = remember { mutableStateOf("") }

        val radioOptions = listOf("Mężczyzna", "Kobieta")
        val (selectedOption, onOptionSelected) = remember { mutableStateOf<String?>(null) }

        // TODO: add validation
        fun addClimber() = coroutineScope.launch {
            val id =
                (database.getAllClimbers().lastOrNull { it.id.contains("M") }?.id?.split("-")?.get(0)?.toInt() ?: 0) + 1
            val climber = Climber(
                climberId = "${id}-M",
                name = name.value,
                sex = when (selectedOption) {
                    "Mężczyzna" -> Sex.MAN
                    "Kobieta" -> Sex.WOMAN
                    else -> null
                },
                dateOfBirth = birthYear.value,
                country = country.value,
                recordType = RecordType.UNOFFICIAL
            )
            database.writeClimber(climber)
            onConfirmButtonClicked()
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                label = { Text("Imię i nazwisko") },
                value = name.value,
                modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                onValueChange = { name.value = it },
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                radioOptions.forEach { text ->
                    Row(
                        Modifier
                            .width(IntrinsicSize.Min)
                            .height(40.dp)
                            .selectable(
                                selected = (text == selectedOption),
                                onClick = {
                                    onOptionSelected(text)
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
                                selected = (text == selectedOption),
                                onClick = { onOptionSelected(text) },
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
                value = birthYear.value,
                modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                onValueChange = { birthYear.value = it },
            )

            TextField(
                label = { Text("Kraj") },
                value = country.value,
                modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                onValueChange = { country.value = it },
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = ::addClimber,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Dodaj")
            }

        }

    }

}

