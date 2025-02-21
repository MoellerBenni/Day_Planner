package com.example.dayplanner.ui.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dayplanner.ui.theme.DayPlannerTheme

/**
 * displays a [LoadingIndicator] with a text beneath it
 * @param text the text to be displayed
 * @param modifier the [Modifier for this Composable]
 * @param textColor the [Color] for the [text]
 */
@Composable
fun LoadingIndicator(text: String, modifier: Modifier = Modifier, textColor: Color = MaterialTheme.colorScheme.onBackground) {
    Column(modifier = modifier, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(
            modifier = Modifier
                .padding(bottom = 24.dp)
                .size(92.dp)
        )
        Text(text = text, style = MaterialTheme.typography.headlineLarge, color = textColor)
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingIndicator_Preview() {
    DayPlannerTheme {
        val text = "Sample Text"
        LoadingIndicator(text = text, modifier = Modifier.fillMaxSize())
    }
}