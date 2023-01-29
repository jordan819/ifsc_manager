package ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ErrorDialog(
    error: MutableState<ErrorDisplay>,
) {
    Dialog(
        title = "Wystąpił błąd",
        content = {
            MaterialTheme {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier.width(300.dp).height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error.value.message,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        },
        onCloseRequest = { error.value = ErrorDisplay("", false) },
    )
}
