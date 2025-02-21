package com.example.dayplanner.ui.screens.taskScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dayplanner.DayPlannerApplication
import com.example.dayplanner.data.repositories.TaskRepository
import com.example.dayplanner.data.useCases.TaskNameValidationResult
import com.example.dayplanner.data.useCases.ValidateTaskNameUseCase
import com.example.dayplanner.model.TimeFrame
import com.example.dayplanner.model.WeekDayTimeFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime


/**
 * ui state for the [TaskViewModel]
 */
sealed interface TaskUiState {
    data object Loading : TaskUiState
    data object TaskNotFound : TaskUiState
    data class EditTask(val taskState: TaskState, val timeFrameState: TimeFrameState?) : TaskUiState
    data object Saving : TaskUiState
    data object Saved : TaskUiState
}

/**
 * ViewModel that can create tasks or edit old ones
 * @param taskRepository the [TaskRepository] to save and retrieve tasks
 * @param validateTaskNameUseCase use case to validate task names
 * @param taskName the name of the task to be edited, if null new task is created
 */
class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val validateTaskNameUseCase: ValidateTaskNameUseCase,
    taskName: String?
) : ViewModel() {

    var uiState: TaskUiState by mutableStateOf(TaskUiState.Loading)
        private set
    private val canTaskNameBeEdited = taskName == null //when task comes from database, it's name should not be edited, since it is its primary key


    companion object {
        fun factory(taskName: String? = null): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as DayPlannerApplication)
                val taskRepository = application.container.taskRepository
                val validateTaskNameUseCase = application.container.validateTaskNameUseCase
                TaskViewModel(taskRepository = taskRepository, validateTaskNameUseCase = validateTaskNameUseCase, taskName = taskName)
            }
        }
    }

    init {
        viewModelScope.launch {
            if (taskName == null) {
                val taskState = TaskState(
                    taskName = "",
                    TaskNameValidationResult.InvalidName,
                    isTaskNameEditable = true,
                    weekDayTimeFrames = emptySet(),
                    weekDayTimeFrameValidity = WeekDayTimeFrameValidity.Empty,
                    isSavingPossible = false
                )
                uiState = TaskUiState.EditTask(taskState = taskState, timeFrameState = null)
                return@launch
            }

            val doesTaskExist = taskRepository.doesTaskAlreadyExist(taskName)
            if (!doesTaskExist) {
                uiState = TaskUiState.TaskNotFound
                return@launch
            }

            val taskTimes = taskRepository.getWeekDayTimeFramesOfTask(taskName)
            val taskState = TaskState(
                taskName = taskName,
                TaskNameValidationResult.Valid,
                isTaskNameEditable = false,
                weekDayTimeFrames = taskTimes.toSet(),
                weekDayTimeFrameValidity = WeekDayTimeFrameValidity.Valid,
                isSavingPossible = true
            )
            uiState = TaskUiState.EditTask(taskState = taskState, timeFrameState = null)
        }
    }

    /**
     * handles the [TaskIntent] for this ViewModel
     * @throws IllegalStateException if the [uiState] is not a [TaskUiState.EditTask]
     *
     */
    fun handleIntent(taskIntent: TaskIntent) {
        val currentUiState = checkUiState()
        when (taskIntent) {
            is TaskIntent.ChangeTaskName -> {
                check(currentUiState.taskState.isTaskNameEditable) { "Changing the name of a Task during editing is not possible" }
                changeTaskState(newTaskName = taskIntent.newTaskName, newTimeFrames = currentUiState.taskState.weekDayTimeFrames)
            }

            is TaskIntent.DeleteWeekDayTimeFrames -> {
                val newWeekDayTimeFrames = currentUiState.taskState.weekDayTimeFrames - taskIntent.weekDayTimeFrames
                changeTaskState(newTaskName = currentUiState.taskState.taskName, newTimeFrames = newWeekDayTimeFrames)
            }

            is TaskIntent.ChangeTimeFrameState -> changeTimeFrameState(
                newStartTime = taskIntent.newStartTime,
                newEndTime = taskIntent.newEndTime,
                newWeekDays = taskIntent.newWeekDays
            )

            is TaskIntent.SaveCurrentTimeFrameState -> saveCurrentTimeFrameState()

            TaskIntent.DismissTimeFrameState -> uiState = currentUiState.copy(timeFrameState = null)

            TaskIntent.SaveTask -> saveTask()
        }
    }

    /**
     * changes the [TaskState] of the [uiState]
     * @param newTaskName the new task name
     * @param newTimeFrames the new set of [WeekDayTimeFrame]
     * @throws IllegalStateException if the [uiState] is not a [TaskUiState.EditTask]
     */
    private fun changeTaskState(newTaskName: String, newTimeFrames: Set<WeekDayTimeFrame>) {
        val editTaskUiState = checkUiState()

        val taskNameValidity =
            if (canTaskNameBeEdited) validateTaskNameUseCase(newTaskName) else TaskNameValidationResult.Valid //when we are editing a Task, it is retrieved from the database, and thus it's name is always valid
        val timeFrameValidity = if (newTimeFrames.isNotEmpty()) WeekDayTimeFrameValidity.Valid else WeekDayTimeFrameValidity.Empty
        val isSavingPossible = taskNameValidity == TaskNameValidationResult.Valid && timeFrameValidity == WeekDayTimeFrameValidity.Valid
        val newTaskState = TaskState(
            taskName = newTaskName,
            taskNameValidity = taskNameValidity,
            isTaskNameEditable = canTaskNameBeEdited,
            weekDayTimeFrames = newTimeFrames,
            weekDayTimeFrameValidity = timeFrameValidity,
            isSavingPossible = isSavingPossible
        )
        uiState = editTaskUiState.copy(taskState = newTaskState)
    }

    /**
     * changes the [TimeFrameState] of the [uiState]
     * @param newStartTime the new start time
     * @param newEndTime the new end time
     * @param newWeekDays the new set of [DayOfWeek]
     * @throws IllegalStateException if the [uiState] is not a [TaskUiState.EditTask]
     *
     */
    private fun changeTimeFrameState(newStartTime: LocalTime, newEndTime: LocalTime, newWeekDays: Set<DayOfWeek>) {
        val editTaskUiState = checkUiState()

        val isTimeFrameValid = newStartTime.isBefore(newEndTime)
        val areWeekDaysValid = newWeekDays.isNotEmpty()
        val error =
            if (!isTimeFrameValid) {
                TimeFrameValidity.StartTimeNotBeforeEndTime
            } else if (!areWeekDaysValid) {
                TimeFrameValidity.WeekDaysEmpty
            } else {
                TimeFrameValidity.Valid
            }
        val isSavingPossible = error == TimeFrameValidity.Valid
        val timeFrameState = TimeFrameState(
            startTime = newStartTime,
            endTime = newEndTime,
            weekDays = newWeekDays,
            isSavingPossible = isSavingPossible,
            timeFrameError = error
        )
        uiState = editTaskUiState.copy(timeFrameState = timeFrameState)
    }

    /**
     * saves the current time frame state to the task state
     * @throws IllegalStateException if the [uiState] is not a [TaskUiState.EditTask]
     * @throws IllegalStateException when the current time frame state is null
     * @throws IllegalStateException when saving is not possible
     */
    private fun saveCurrentTimeFrameState() {
        val currentUiState = checkUiState()
        check(currentUiState.timeFrameState != null) { "can't save current time Frame when it is null" }
        check(currentUiState.timeFrameState.isSavingPossible) { "saving current time frame is not allowed when saving is not possible" }
        val timeFrame = TimeFrame(startTime = currentUiState.timeFrameState.startTime, endTime = currentUiState.timeFrameState.endTime)
        val newWeekDayTimeFrames = currentUiState.timeFrameState.weekDays.map { selectedWeekDay ->
            WeekDayTimeFrame(weekDay = selectedWeekDay, timeFrame = timeFrame)
        }.toSet()
        uiState = currentUiState.copy(timeFrameState = null)
        changeTaskState(
            newTaskName = currentUiState.taskState.taskName,
            newTimeFrames = currentUiState.taskState.weekDayTimeFrames + newWeekDayTimeFrames
        )
    }

    /**
     *
     * @throws IllegalStateException if the [uiState] is not a [TaskUiState.EditTask]
     * @throws IllegalStateException if isSavingPossible in the [uiState] is false
     */
    private fun saveTask() {
        val editTaskUiState = checkUiState()
        check(editTaskUiState.taskState.isSavingPossible) { "Saving was tried despite the ui state stating it is not possible" }

        uiState = TaskUiState.Saving
        viewModelScope.launch {
            taskRepository.saveTask(taskName = editTaskUiState.taskState.taskName, weekDayTimeFrames = editTaskUiState.taskState.weekDayTimeFrames)
            delay(500)
            uiState = TaskUiState.Saved
        }
    }

    /**
     * returns the ui state as [TaskUiState.EditTask]
     * @throws IllegalStateException when the [uiState] is a [TaskUiState.EditTask]
     */
    private fun checkUiState(): TaskUiState.EditTask {
        check(uiState is TaskUiState.EditTask) { "ui state must be in EditTask state but was $uiState" }
        return uiState as TaskUiState.EditTask
    }


}