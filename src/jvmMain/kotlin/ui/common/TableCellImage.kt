package ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.TableCellImage(
    image: ImageVector,
    weight: Float,
    onClick: (() -> Unit)? = null,
) {
    if (onClick != null) {
        Icon(
            imageVector = image,
            contentDescription = null,
            modifier = Modifier
                .border(1.dp, Color.Black)
                .weight(weight)
                .height(40.dp)
                .clickable { onClick() }
        )
    } else {
        Icon(
            imageVector = image,
            contentDescription = null,
            modifier = Modifier
                .border(1.dp, Color.Black)
                .weight(weight)
                .height(40.dp)
        )
    }
}
