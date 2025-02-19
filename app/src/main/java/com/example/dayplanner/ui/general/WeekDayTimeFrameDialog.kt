@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.dayplanner.ui.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dayplanner.R
import com.example.dayplanner.ui.theme.DayPlannerTheme
import java.time.DayOfWeek
import java.time.LocalTime

typealias StartTime = LocalTime
typealias EndTime = LocalTime

/**
 * Dialog that lets the user pick start, end date and weekDays
 * @param selectedWeekDays the [DayOfWeek] that are currently selected
 * @param onValueChanged called when either the start date, end date or selected weekdays are changed
 * @param onDismissRequest called when dialog is dismissed
 * @param onConfirm called when the confirm button is clicked
 * @param modifier the [Modifier] for this composable
 * @param confirmEnabled whether the confirm button is enabled
 */
@Composable
fun WeekDayTimeFrameDialog(
    selectedWeekDays: Set<DayOfWeek>,
    onValueChanged: (StartTime, EndTime, Set<DayOfWeek>) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    confirmEnabled: Boolean = true,
    errorMessage: String? = null
) {
    AlertDialog(
        modifier = modifier,
        icon = { Icon(imageVector = Icons.Outlined.DateRange, contentDescription = null) },
        title = {
            Text(text = stringResource(R.string.pick_time_frames))
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = onConfirm, enabled = confirmEnabled) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        text = {
            Column {
                val startTime = rememberTimePickerState()
                val endTime = rememberTimePickerState()
                Text(
                    text = stringResource(R.string.start_time),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )
                TimeInput(state = startTime)
                Text(
                    text = stringResource(R.string.end_time),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )
                TimeInput(state = endTime)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    for (weekDay in DayOfWeek.entries) {
                        WeekDaySelector(
                            weekDay = weekDay,
                            selected = selectedWeekDays.contains(weekDay),
                            onClick = { newValue ->
                                val newWeekDays = if (newValue) selectedWeekDays + weekDay else selectedWeekDays - weekDay
                                onValueChanged(startTime.toLocalTime(), endTime.toLocalTime(), newWeekDays)
                            })
                    }
                }

                Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(25.dp)) {
                    ErrorText(text = errorMessage)
                }


                LaunchedEffect(startTime, endTime) {
                    onValueChanged(startTime.toLocalTime(), endTime.toLocalTime(), selectedWeekDays)
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun WeekDayTimeFrameDialog_Preview() {
    DayPlannerTheme {
        val selectedWeekDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)
        val errorMessage = "test error message"
        WeekDayTimeFrameDialog(selectedWeekDays = selectedWeekDays, onConfirm = {}, onDismissRequest = {}, onValueChanged = { _, _, _ -> }, errorMessage = errorMessage)
    }
}


/**
 * converts the current time of the [TimePickerState] to a [LocalTime]
 */
private fun TimePickerState.toLocalTime() = LocalTime.of(this.hour, this.minute)