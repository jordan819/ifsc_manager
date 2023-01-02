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
    coroutineScope: CoroutineScope
): @Composable () -> Unit {

    return {

        val name = remember { mutableStateOf("") }
        val birthYear = remember { mutableStateOf("") }
        val country = remember { mutableStateOf("") }

        val radioOptions = listOf("Mężczyzna", "Kobieta")
        val (selectedOption, onOptionSelected) = remember { mutableStateOf<String?>(null) }

        // TODO: add validation
        fun addClimber() = coroutineScope.launch {
            val climber = Climber(
                climberId = database.getAllClimbers().last().id + 1,
                name = name.value,
                sex = when (selectedOption) {
                    "Mężczyzna" -> Sex.MAN
                    "Kobieta" -> Sex.WOMAN
                    else -> null
                },
                yearOfBirth = birthYear.value.toIntOrNull(),
                country = country.value,
                recordType = RecordType.UNOFFICIAL
            )
            database.writeClimber(climber)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                label = { Text("Imię i nazwisko") },
                value = name.value,
                modifier = Modifier.weight(1F).width(400.dp).height(IntrinsicSize.Min),
                onValueChange = { name.value = it },
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column() {
                radioOptions.forEach { text ->
                    Row(
                        Modifier
                            .width(IntrinsicSize.Min)
                            .selectable(
                                selected = (text == selectedOption),
                                onClick = {
                                    onOptionSelected(text)
                                }
                            )
                            .padding(horizontal = 16.dp)
                    ) {
                        RadioButton(
                            selected = (text == selectedOption),
                            onClick = { onOptionSelected(text) }
                        )
                        Text(
                            text = text,
                            style = MaterialTheme.typography.body1.merge(),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            TextField(
                label = { Text("Rok urodzenia") },
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

