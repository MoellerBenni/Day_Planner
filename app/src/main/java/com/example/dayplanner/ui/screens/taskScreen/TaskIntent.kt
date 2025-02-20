package com.example.dayplanner.ui.screens.taskScreen

import com.example.dayplanner.model.WeekDayTimeFrame
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * intents for the [TaskViewModel]
 */
sealed interface TaskIntent {
    data class ChangeTaskName(val newTaskName: String) : TaskIntent
    data class DeleteWeekDayTimeFrames(val weekDayTimeFrames: Set<WeekDayTimeFrame>): TaskIntent
    data class ChangeTimeFrameState(val newStartTime: LocalTime, val newEndTime: LocalTime, val newWeekDays: Set<DayOfWeek>) : TaskIntent
    data object DismissTimeFrameState: TaskIntent
    data object SaveCurrentTimeFrameState: TaskIntent
    data object SaveTask : TaskIntent
}