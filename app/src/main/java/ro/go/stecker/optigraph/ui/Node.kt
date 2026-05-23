package ro.go.stecker.optigraph.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun Node(
    radius: Dp,
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.LightGray,
    borderWidth: Dp = 2.dp,
    borderColor: Color = Color.Black,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(color, CircleShape)
            .border(width = borderWidth, color = borderColor, shape = CircleShape)
            .size((2 * radius.value).dp)
    ) {
        Text(text = text, color = Color.Black)
    }
}