package ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ErrorDialog(
    error: MutableState<ErrorDisplay>,
) {

    val message = error.value.message ?: return
    val uriHandler = LocalUriHandler.current

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
                        ClickableText(
                            text = message,
                            style = TextStyle(
                                textAlign = TextAlign.Center
                            ),
                            onClick = {
                                message
                                    .getStringAnnotations("URL", it, it)
                                    .firstOrNull()?.let { stringAnnotation ->
                                        uriHandler.openUri(stringAnnotation.item)
                                    }
                            },
                        )
                    }
                }
            }
        },
        onCloseRequest = { error.value = ErrorDisplay(null) },
    )
}
