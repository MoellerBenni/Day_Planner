package com.example.dayplanner.ui.theme.screens.taskScreen

import com.example.dayplanner.data.useCases.TaskNameValidationResult
import com.example.dayplanner.model.WeekDayTimeFrame

/**
 * data class that models the task state for the [TaskUiState]
 */
data class TaskState(
    val taskName: String,
    val taskNameValidity: TaskNameValidationResult,
    val isTaskNameEditable: Boolean,
    val weekDayTimeFrames: Set<WeekDayTimeFrame>,
    val weekDayTimeFrameValidity: WeekDayTimeFrameValidity,
    val isSavingPossible: Boolean,
)

/**
 * result for checking if a list of [WeekDayTimeFrame] are valid for a task
 */
enum class WeekDayTimeFrameValidity {
    Valid, Empty
}

