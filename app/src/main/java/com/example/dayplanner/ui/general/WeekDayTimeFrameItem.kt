package com.example.dayplanner.ui.general

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.dayplanner.R
import com.example.dayplanner.data.database.entities.testTaskTimeEntities
import com.example.dayplanner.model.WeekDayTimeFrame
import com.example.dayplanner.ui.theme.DayPlannerTheme

/**
 * [ListItem] that displays a [WeekDayTimeFrame]
 * @param weekDayTimeFrame the [WeekDayTimeFrame] to be displayed
 * @param onDeleteClicked called when the delete icon is clicked
 * @param modifier the [Modifier] for this Composable
 */
@Composable
fun WeekDayTimeFrameItem(weekDayTimeFrame: WeekDayTimeFrame, onDeleteClicked: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(text = weekDayTimeFrame.weekDay.name) },
        supportingContent = { Text(text = "${weekDayTimeFrame.timeFrame.startTime} - ${weekDayTimeFrame.timeFrame.endTime}") },
        leadingContent = { Icon(painter = painterResource(R.drawable.repeat), contentDescription = null) },
        trailingContent = {
            IconButton(onClick = onDeleteClicked)
            {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = "delete $weekDayTimeFrame")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun WeekDayTimeFrameItem_Preview() {
    DayPlannerTheme {
        val weekDayTimeFrame = testTaskTimeEntities.first().toWeekDayTimeFrame()
        WeekDayTimeFrameItem(weekDayTimeFrame = weekDayTimeFrame, onDeleteClicked = {})
    }
}