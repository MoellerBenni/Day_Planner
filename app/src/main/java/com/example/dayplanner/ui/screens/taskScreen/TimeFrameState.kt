package com.example.dayplanner.ui.screens.taskScreen

import com.example.dayplanner.model.TimeFrame
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * data class that represents the time frame state for the [TaskUiState]
 */
data class TimeFrameState(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val timeValidity: TimeFrameValidity,
    val weekDays: Set<DayOfWeek>,
    val weekDayValidity: WeekDayValidity,
    val isSavingPossible: Boolean,
)

/**
 * result of checking if a [TimeFrame] is valid for a Task
 */
enum class TimeFrameValidity {
    Valid, StartNotBeforeEnd
}

/**
 * result of checking if a list of [DayOfWeek] are valid for a Task
 */
enum class WeekDayValidity {
    Valid, Empty
}
