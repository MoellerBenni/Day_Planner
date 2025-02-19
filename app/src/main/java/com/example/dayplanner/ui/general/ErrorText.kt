package com.example.dayplanner.ui.general

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dayplanner.ui.theme.DayPlannerTheme

/**
 * Composable that display [text] as an Error
 * @param text the text to be displayed, when set to null the last error vanishes
 * @param modifier the [Modifier] for this Composable
 */
@Composable
fun ErrorText(text: String?, modifier: Modifier = Modifier) {

    var rememberedText by remember { mutableStateOf("") }
    LaunchedEffect(text) {
        if (text != null) {
            rememberedText = text
        }
    }
    //without rememberedText when text is null, the Text() inside the AnimatedVisibility() would disappear instantly before playing the exitTransition


    AnimatedVisibility(
        visible = text != null,
        modifier = modifier,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 500)
        ) + slideInVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessMedium,
            ), initialOffsetY = { -it }
        ),
        exit = fadeOut(animationSpec = tween(durationMillis = 250))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .testTag("warning icon")
            )
            Text(
                text = rememberedText,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorText_Preview() {
    DayPlannerTheme {
        val text = "Test Error"
        ErrorText(text = text, modifier = Modifier.padding(16.dp))
    }
}