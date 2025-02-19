package com.example.dayplanner.ui.screens.taskScreen

import com.example.dayplanner.model.WeekDayTimeFrame
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * data class that represents the time frame state for the [TaskUiState]
 */
data class TimeFrameState(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val weekDays: Set<DayOfWeek>,
    val isSavingPossible: Boolean,
    val timeFrameError: TimeFrameValidity
)

/**
 * result of checking if a [WeekDayTimeFrame] is valid for a Task
 */
enum class TimeFrameValidity {
    Valid, StartTimeNotBeforeEndTime, WeekDaysEmpty
}

