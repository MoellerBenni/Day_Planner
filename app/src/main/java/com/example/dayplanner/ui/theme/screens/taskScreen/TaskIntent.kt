package com.example.dayplanner.ui.theme.screens.taskScreen

import com.example.dayplanner.model.WeekDayTimeFrame
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * intents for the [TaskViewModel]
 */
sealed interface TaskIntent {
    data class ChangeTaskState(val newTaskName: String, val newWeekDayTimeFrames: Set<WeekDayTimeFrame>) : TaskIntent
    data class ChangeTimeFrameState(val newStartTime: LocalTime, val newEndTime: LocalTime, val newWeekDays: Set<DayOfWeek>) : TaskIntent
    data object SaveTask : TaskIntent
}