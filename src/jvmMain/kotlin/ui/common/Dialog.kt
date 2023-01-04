package ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState

@Composable
fun Dialog(
    title: String = "",
    content: @Composable () -> Unit,
    onCloseRequest: () -> Unit,
) {

    Dialog(
        title = "",
        state = DialogState(size = DpSize.Companion.Unspecified),
        onCloseRequest = onCloseRequest
    ) {
        Card(
            elevation = 8.dp,
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .height(IntrinsicSize.Min)
            ) {
                ProvideTextStyle(MaterialTheme.typography.subtitle1) {
                    Text(text = title)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.weight(1F)) {
                    content()
                }

            }
        }
    }

}
