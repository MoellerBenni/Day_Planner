package com.example.dayplanner.ui.general

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dayplanner.ui.theme.DayPlannerTheme

/**
 * Composable that display an [imageVector] and [text] centered in the middle
 * @param imageVector the icon to be displayed
 * @param text the text to be displayed underneath
 * @param modifier the [Modifier] for this Composable
 * @param onClick called when either the Text or icon are clicked
 * @param color the [Color] for the icon and text
 */
@Composable
fun IconAndText(
    imageVector: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    Column(
        modifier = modifier.clickable { onClick() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = imageVector, contentDescription = null, modifier = Modifier
                .padding(bottom = 24.dp)
                .size(92.dp),
            tint = color
        )
        Text(text = text, style = MaterialTheme.typography.headlineLarge, color = color, textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true)
@Composable
private fun IconAndText_Preview() {
    DayPlannerTheme {
        val icon = Icons.Outlined.DateRange
        val text = "This is a sample text that could be displayed underneath the icon"
        IconAndText(imageVector = icon, text = text, modifier = Modifier.fillMaxSize())
    }
}