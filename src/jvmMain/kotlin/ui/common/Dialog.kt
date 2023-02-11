package ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import utils.AppColors

@Composable
fun Dialog(
    title: String = "",
    content: @Composable () -> Unit,
    onCloseRequest: () -> Unit,
) {

    Dialog(
        title = title,
        state = DialogState(size = DpSize.Companion.Unspecified),
        onCloseRequest = onCloseRequest
    ) {
        Card {
            Column(
                modifier = Modifier
                    .background(AppColors.Gray)
                    .padding(8.dp)
                    .height(IntrinsicSize.Min)
            ) {
                Box(modifier = Modifier.weight(1F)) {
                    content()
                }
            }
        }
    }

}
