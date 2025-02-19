package com.example.dayplanner.ui.general

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dayplanner.ui.theme.DayPlannerTheme
import java.time.DayOfWeek

/**
 * Select for a [DayOfWeek]
 *  @param weekDay the [DayOfWeek] for this Selector
 *  @param selected whether this is selected or not
 *  @param onClick called when this Composable is clicked
 *  @param modifier the [Modifier] for this Composable
 */
@Composable
fun WeekDaySelector(weekDay: DayOfWeek, selected: Boolean, onClick: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val backGroundColor by
    animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        },
        animationSpec = tween(durationMillis = 250)
    )

    val textColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(250)
    )

    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .toggleable(value = selected, enabled = true, onValueChange = onClick, interactionSource = interactionSource, indication = null)
            .size(25.dp)
            .background(color = backGroundColor, shape = CircleShape), contentAlignment = Alignment.Center
    ) {
        Text(text = weekDay.name.first().toString(), color = textColor, style = MaterialTheme.typography.labelMedium)
    }
}

@Preview(showBackground = true)
@Composable
private fun WeekDaySelector_Preview() {
    DayPlannerTheme {
        Row {
            val selectedList = remember { mutableStateListOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY) }

            val modifier = Modifier.padding(8.dp)
            DayOfWeek.entries.forEach { dayOfWeek ->
                WeekDaySelector(weekDay = dayOfWeek, selected = selectedList.contains(dayOfWeek), onClick = {
                    if (it) selectedList.add(dayOfWeek) else selectedList.remove(dayOfWeek)
                }, modifier = modifier)
            }
        }
    }
}