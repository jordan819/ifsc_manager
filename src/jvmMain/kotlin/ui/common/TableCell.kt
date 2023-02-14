package ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import utils.AppColors

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
) {
    Box(
        Modifier
            .border(1.dp, Color.Black)
            .background(AppColors.Gray)
            .weight(weight)
            .height(40.dp)
            .align(Alignment.CenterVertically),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun RowScope.TableCell(
    image: ImageVector,
    weight: Float,
    onClick: (() -> Unit),
) {
    Icon(
        imageVector = image,
        contentDescription = null,
        modifier = Modifier
            .border(1.dp, Color.Black)
            .background(AppColors.Gray)
            .weight(weight)
            .height(40.dp)
            .clickable { onClick() }
    )
}
