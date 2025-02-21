package com.example.dayplanner.ui.screens.taskScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dayplanner.R
import com.example.dayplanner.data.database.entities.testTaskTimeEntities
import com.example.dayplanner.data.useCases.TaskNameValidationResult
import com.example.dayplanner.ui.general.ErrorText
import com.example.dayplanner.ui.general.IconAndText
import com.example.dayplanner.ui.general.LoadingIndicator
import com.example.dayplanner.ui.general.TaskNameTextField
import com.example.dayplanner.ui.general.WeekDayTimeFrameDialog
import com.example.dayplanner.ui.general.WeekDayTimeFrameItem
import com.example.dayplanner.ui.theme.DayPlannerTheme
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalTime

@Composable
fun TaskScreen(uiState: TaskUiState, onIntent: (TaskIntent) -> Unit, onNavigateBack: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            when (uiState) {
                is TaskUiState.EditTask -> TaskScreenEdit(
                    uiState = uiState,
                    onIntent = onIntent,
                    onNavigateBack = onNavigateBack,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                )

                TaskUiState.Loading -> LoadingIndicator(text = stringResource(R.string.loading_task), modifier = Modifier.fillMaxSize())
                TaskUiState.TaskNotFound -> IconAndText(
                    imageVector = Icons.Outlined.Close,
                    text = stringResource(R.string.task_was_not_found),
                    modifier = Modifier.fillMaxSize()
                )

                TaskUiState.Saving -> LoadingIndicator(text = stringResource(R.string.saving_task), modifier = Modifier.fillMaxSize())
                TaskUiState.Saved -> IconAndText(
                    imageVector = Icons.Outlined.Check,
                    text = stringResource(R.string.task_was_saved),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        LaunchedEffect(uiState) {
            if (uiState is TaskUiState.TaskNotFound || uiState is TaskUiState.Saved) {
                delay(500)
                onNavigateBack()
            }
        }
    }
}

@Composable
private fun TaskScreenEdit(uiState: TaskUiState.EditTask, onIntent: (TaskIntent) -> Unit, onNavigateBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        val errorMessage = when (uiState.taskState.taskNameValidity) {
            TaskNameValidationResult.Valid -> null
            TaskNameValidationResult.InvalidName -> stringResource(R.string.task_name_is_invalid)
            TaskNameValidationResult.NameAlreadyUsed -> stringResource(R.string.task_name_is_already_used)
        }

        TaskNameTextField(
            modifier = Modifier.fillMaxWidth(),
            text = uiState.taskState.taskName,
            onTextChanged = {
                val changeIntent = TaskIntent.ChangeTaskName(it)
                onIntent(changeIntent)
            },
            errorMessage = errorMessage,
            readOnly = !uiState.taskState.isTaskNameEditable
        )

        Row(
            modifier = Modifier
                .padding(top = 24.dp, start = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.DateRange, contentDescription = null, modifier = Modifier
                    .padding(end = 16.dp)
                    .size(24.dp)
            )
            Text(text = "Time Frames", style = MaterialTheme.typography.titleLarge)
        }


        Card(
            modifier = Modifier
                .weight(1f)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(uiState.taskState.weekDayTimeFrames.toList(), key = { it.toString() }) { weekDayTimeFrame ->
                    WeekDayTimeFrameItem(
                        modifier = Modifier.animateItem(),
                        weekDayTimeFrame = weekDayTimeFrame,
                        onDeleteClicked = {
                            val deleteIntent = TaskIntent.DeleteWeekDayTimeFrames(setOf(weekDayTimeFrame))
                            onIntent(deleteIntent)
                        })
                }

                if (uiState.taskState.weekDayTimeFrames.isEmpty()) {
                    item(key = "Show WeekDayTimeFrameDialog") {
                        IconAndText(
                            imageVector = Icons.Outlined.DateRange,
                            text = "Add TimeFrames",
                            modifier = Modifier
                                .fillParentMaxSize()
                                .animateItem(),
                            onClick = {
                                val changeIntent = TaskIntent.ChangeTimeFrameState(
                                    newStartTime = LocalTime.MIDNIGHT,
                                    newEndTime = LocalTime.MIDNIGHT,
                                    newWeekDays = emptySet()
                                )
                                onIntent(changeIntent)
                            })
                    }
                }
            }

            val timeFrameErrorMessage = when (uiState.taskState.weekDayTimeFrameValidity) {
                WeekDayTimeFrameValidity.Valid -> null
                WeekDayTimeFrameValidity.Empty -> stringResource(R.string.time_frames_must_not_be_empty)
            }
            ErrorText(text = timeFrameErrorMessage, modifier = Modifier.padding(top = 16.dp, start = 8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = {
                    val deleteIntent = TaskIntent.DeleteWeekDayTimeFrames(uiState.taskState.weekDayTimeFrames)
                    onIntent(deleteIntent)
                }) {
                    Text(stringResource(R.string.remove_all), style = MaterialTheme.typography.labelLarge)
                }

                TextButton(onClick = {
                    val changeIntent = TaskIntent.ChangeTimeFrameState(
                        newStartTime = LocalTime.MIDNIGHT,
                        newEndTime = LocalTime.MIDNIGHT,
                        newWeekDays = emptySet()
                    )
                    onIntent(changeIntent)
                }) {
                    Text("Add new Time Frames", style = MaterialTheme.typography.labelLarge)
                }
            }

        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 36.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onNavigateBack) {
                Text(text = stringResource(R.string.cancel), style = MaterialTheme.typography.titleMedium)
            }
            Button(onClick = {
                val saveIntent = TaskIntent.SaveTask
                onIntent(saveIntent)
            }, enabled = uiState.taskState.isSavingPossible) {
                Text(stringResource(R.string.save_task), style = MaterialTheme.typography.titleMedium)
            }
        }

        uiState.timeFrameState?.let { timeFrameState ->
            WeekDayTimeFrameDialog(selectedWeekDays = timeFrameState.weekDays,
                onValueChanged = { startTime, endTime, weekDays ->
                    val changeIntent = TaskIntent.ChangeTimeFrameState(newStartTime = startTime, newEndTime = endTime, newWeekDays = weekDays)
                    onIntent(changeIntent)
                },
                onDismissRequest = {
                    val dismissIntent = TaskIntent.DismissTimeFrameState
                    onIntent(dismissIntent)
                },
                onConfirm = {
                    val saveIntent = TaskIntent.SaveCurrentTimeFrameState
                    onIntent(saveIntent)
                })
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskScreen_Preview() {
    DayPlannerTheme {
        val timeFrames = testTaskTimeEntities.map { it.toWeekDayTimeFrame() }
        val taskState = TaskState(
            taskName = "test task name",
            taskNameValidity = TaskNameValidationResult.InvalidName,
            isTaskNameEditable = true,
            weekDayTimeFrames = timeFrames.toSet(),
            weekDayTimeFrameValidity = WeekDayTimeFrameValidity.Empty,
            isSavingPossible = true
        )
        val timeFrameState = TimeFrameState(
            startTime = LocalTime.MIDNIGHT,
            endTime = LocalTime.MIDNIGHT,
            weekDays = setOf(DayOfWeek.WEDNESDAY),
            timeFrameError = TimeFrameValidity.StartTimeNotBeforeEndTime,
            isSavingPossible = false
        )
        val uiState = TaskUiState.EditTask(taskState = taskState, timeFrameState = timeFrameState)
        TaskScreen(uiState = uiState, onIntent = {}, onNavigateBack = {}, modifier = Modifier.fillMaxSize())
    }
}