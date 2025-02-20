package com.example.dayplanner.ui.theme.screens.taskScreen

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.dayplanner.MainDispatcherRule
import com.example.dayplanner.data.database.entities.testTaskEntities
import com.example.dayplanner.data.database.entities.testTaskTimeEntities
import com.example.dayplanner.data.database.getPrePopulatedDatabase
import com.example.dayplanner.data.repositories.DatabaseTaskRepository
import com.example.dayplanner.data.repositories.TaskRepository
import com.example.dayplanner.data.useCases.TaskNameValidationResult
import com.example.dayplanner.data.useCases.ValidateTaskNameUseCase
import com.example.dayplanner.model.TimeFrame
import com.example.dayplanner.model.WeekDayTimeFrame
import com.example.dayplanner.ui.screens.taskScreen.TaskIntent
import com.example.dayplanner.ui.screens.taskScreen.TaskState
import com.example.dayplanner.ui.screens.taskScreen.TaskUiState
import com.example.dayplanner.ui.screens.taskScreen.TaskViewModel
import com.example.dayplanner.ui.screens.taskScreen.TimeFrameState
import com.example.dayplanner.ui.screens.taskScreen.TimeFrameValidity
import com.example.dayplanner.ui.screens.taskScreen.WeekDayTimeFrameValidity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var context: Context
    private lateinit var taskRepository: TaskRepository
    private lateinit var taskViewModel: TaskViewModel
    private val taskEntities = testTaskEntities
    private val taskTimeEntities = testTaskTimeEntities
    private val testTask = taskEntities.first().name
    private val testUiState = TaskUiState.EditTask(
        taskState = TaskState(
            taskName = testTask,
            taskNameValidity = TaskNameValidationResult.Valid,
            isTaskNameEditable = false,
            weekDayTimeFrames = taskTimeEntities.filter { it.taskName == testTask }.map { it.toWeekDayTimeFrame() }.toSet(),
            weekDayTimeFrameValidity = WeekDayTimeFrameValidity.Valid,
            isSavingPossible = true
        ), timeFrameState = null
    )

    @Before
    fun createContext() {
        context = ApplicationProvider.getApplicationContext()
    }

    private fun createTaskViewModel(
        taskName: String? = testTask,
        weekDayTimeFramesToAdd: Set<WeekDayTimeFrame> = emptySet(),
        navigateToSavedState: Boolean = false
    ) = runTest {
        val dao = getPrePopulatedDatabase(
            context = context,
            taskEntities = taskEntities,
            taskTimeEntities = taskTimeEntities,
            executor = mainDispatcherRule.testDispatcher.asExecutor()
        )
        taskRepository = DatabaseTaskRepository(taskDao = dao)
        val validateTaskNameUseCase = ValidateTaskNameUseCase(
            taskRepository = taskRepository,
            coroutineScope = this.backgroundScope,
            dispatcher = mainDispatcherRule.testDispatcher
        )
        taskViewModel = TaskViewModel(taskRepository = taskRepository, validateTaskNameUseCase = validateTaskNameUseCase, taskName = taskName)
        runCurrent()

        for (timeFrame in weekDayTimeFramesToAdd.map { it.timeFrame }.distinct()) {
            val weekDaysOfTimeFrame = weekDayTimeFramesToAdd.filter { it.timeFrame == timeFrame }.map { it.weekDay }
            val changeIntent = TaskIntent.ChangeTimeFrameState(
                newStartTime = timeFrame.startTime,
                newEndTime = timeFrame.endTime,
                newWeekDays = weekDaysOfTimeFrame.toSet()
            )
            taskViewModel.handleIntent(changeIntent)
            taskViewModel.handleIntent(TaskIntent.SaveCurrentTimeFrameState)
        }

        if (navigateToSavedState) {
            taskViewModel.handleIntent(TaskIntent.SaveTask)
            advanceUntilIdle()
        }
    }

    @Test
    fun createViewModel_NewTask() = runTest {
        createTaskViewModel(taskName = null)
        advanceUntilIdle()
        val expectedUiState = TaskUiState.EditTask(
            TaskState(
                taskName = "",
                taskNameValidity = TaskNameValidationResult.InvalidName,
                isTaskNameEditable = true,
                weekDayTimeFrames = emptySet(),
                weekDayTimeFrameValidity = WeekDayTimeFrameValidity.Empty,
                isSavingPossible = false
            ), timeFrameState = null
        )
        assertEquals(expectedUiState, taskViewModel.uiState)
    }

    @Test
    fun createViewModel_TaskFound() = runTest {
        createTaskViewModel()
        advanceUntilIdle()
        val expectedUiState = testUiState
        assertEquals(expectedUiState, taskViewModel.uiState)
    }

    @Test
    fun createViewModel_TaskNotFound() = runTest {
        createTaskViewModel(taskName = "invalid Task")
        advanceUntilIdle()
        val expectedUiState = TaskUiState.TaskNotFound
        assertEquals(expectedUiState, taskViewModel.uiState)
    }

    @Test
    fun changeTaskName_ValidTaskName() = runTest {
        createTaskViewModel(taskName = null)
        val taskName = "valid task name"
        val changeIntent = TaskIntent.ChangeTaskName(newTaskName = taskName)
        taskViewModel.handleIntent(changeIntent)
        val expectedUiState = TaskUiState.EditTask(
            TaskState(
                taskName = taskName,
                taskNameValidity = TaskNameValidationResult.Valid,
                isTaskNameEditable = true,
                weekDayTimeFrames = emptySet(),
                weekDayTimeFrameValidity = WeekDayTimeFrameValidity.Empty,
                isSavingPossible = false
            ), timeFrameState = null
        )
        assertEquals(expectedUiState, taskViewModel.uiState)
    }

    @Test
    fun changeTaskName_InvalidTaskName() = runTest {
        createTaskViewModel(taskName = null)
        val taskName = ""
        val changeIntent = TaskIntent.ChangeTaskName(newTaskName = taskName)
        taskViewModel.handleIntent(changeIntent)
        val expectedUiState = TaskUiState.EditTask(
            TaskState(
                taskName = taskName,
                taskNameValidity = TaskNameValidationResult.InvalidName,
                isTaskNameEditable = true,
                weekDayTimeFrames = emptySet(),
                weekDayTimeFrameValidity = WeekDayTimeFrameValidity.Empty,
                isSavingPossible = false
            ), timeFrameState = null
        )
        assertEquals(expectedUiState, taskViewModel.uiState)
    }

    @Test
    fun changeTaskName_TaskNameAlreadyUsed() = runTest {
        createTaskViewModel(taskName = null)
        val taskName = testTaskEntities.first().name
        val changeIntent = TaskIntent.ChangeTaskName(newTaskName = taskName)
        taskViewModel.handleIntent(changeIntent)
        val expectedUiState = TaskUiState.EditTask(
            TaskState(
                taskName = taskName,
                taskNameValidity = TaskNameValidationResult.NameAlreadyUsed,
                isTaskNameEditable = true,
                weekDayTimeFrames = emptySet(),
                weekDayTimeFrameValidity = WeekDayTimeFrameValidity.Empty,
                isSavingPossible = false
            ), timeFrameState = null
        )
        assertEquals(expectedUiState, taskViewModel.uiState)
    }

    @Test
    fun changeTaskName_InvalidState() = runTest {
        createTaskViewModel(navigateToSavedState = true)
        val taskName = testTaskEntities.first().name
        val changeIntent = TaskIntent.ChangeTaskName(newTaskName = taskName)
        val exception = assertFailsWith<IllegalStateException> { taskViewModel.handleIntent(changeIntent) }
        val expectedMessage = "ui state must be in EditTask state but was ${taskViewModel.uiState}"
        assertEquals(expectedMessage, exception.message)
    }

    @Test
    fun changeTaskName_EditingTask() = runTest {
        createTaskViewModel()
        val taskName = testTaskEntities.first().name
        val changeIntent = TaskIntent.ChangeTaskName(newTaskName = taskName)
        val exception = assertFailsWith<IllegalStateException> { taskViewModel.handleIntent(changeIntent) }
        val expectedMessage = "Changing the name of a Task during editing is not possible"
        assertEquals(expectedMessage, exception.message)
    }

    @Test
    fun deleteWeekDayTimeFrames() {
        createTaskViewModel()
        val timeFrameToDelete = testUiState.taskState.weekDayTimeFrames.first()
        val deleteIntent = TaskIntent.DeleteWeekDayTimeFrames(setOf(timeFrameToDelete))
        taskViewModel.handleIntent(deleteIntent)
        val expectedTaskState = testUiState.taskState.copy(weekDayTimeFrames = testUiState.taskState.weekDayTimeFrames - timeFrameToDelete)
        val expectedUiState = testUiState.copy(taskState = expectedTaskState)
        assertEquals(expectedUiState, taskViewModel.uiState)
    }

    @Test
    fun deleteWeekDayTimeFrames_All() {
        createTaskViewModel()
        val timeFrameToDelete = testUiState.taskState.weekDayTimeFrames
        val deleteIntent = TaskIntent.DeleteWeekDayTimeFrames(timeFrameToDelete)
        taskViewModel.handleIntent(deleteIntent)
        val expectedTaskState = testUiState.taskState.copy(weekDayTimeFrames = emptySet(), isSavingPossible = false, weekDayTimeFrameValidity = WeekDayTimeFrameValidity.Empty)
        val expectedUiState = testUiState.copy(taskState = expectedTaskState)
        assertEquals(expectedUiState, taskViewModel.uiState)
    }

    @Test
    fun changeTimeFrameState_Valid() = runTest {
        createTaskViewModel()
        val startTime = LocalTime.of(10, 0)
        val endTime = LocalTime.of(11, 0)
        val weekDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)
        val changeIntent = TaskIntent.ChangeTimeFrameState(newStartTime = startTime, newEndTime = endTime, newWeekDays = weekDays)
        taskViewModel.handleIntent(changeIntent)
        val expectedTimeFrameState = TimeFrameState(
            startTime = startTime,
            endTime = endTime,
            weekDays = weekDays,
            isSavingPossible = true,
            timeFrameError = TimeFrameValidity.Valid
        )
        val expectedUiState = testUiState.copy(timeFrameState = expectedTimeFrameState)
        assertEquals(expectedUiState, taskViewModel.uiState)
    }

    @Test
    fun changeTimeFrameState_InvalidStartEndTime() = runTest {
        createTaskViewModel()
        val startTime = LocalTime.of(10, 0)
        val weekDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)
        val changeIntent = TaskIntent.ChangeTimeFrameState(newStartTime = startTime, newEndTime = startTime, newWeekDays = weekDays)
        taskViewModel.handleIntent(changeIntent)
        val expectedTimeFrameState = TimeFrameState(
            startTime = startTime,
            endTime = startTime,
            weekDays = weekDays,
            isSavingPossible = false,
            timeFrameError = TimeFrameValidity.StartTimeNotBeforeEndTime
        )
        val expectedUiState = testUiState.copy(timeFrameState = expectedTimeFrameState)
        assertEquals(expectedUiState, taskViewModel.uiState)
    }

    @Test
    fun changeTimeFrameState_WeekDaysEmpty() = runTest {
        createTaskViewModel()
        val startTime = LocalTime.of(10, 0)
        val endTime = LocalTime.of(11, 0)
        val weekDays = setOf<DayOfWeek>()
        val changeIntent = TaskIntent.ChangeTimeFrameState(newStartTime = startTime, newEndTime = endTime, newWeekDays = weekDays)
        taskViewModel.handleIntent(changeIntent)
        val expectedTimeFrameState = TimeFrameState(
            startTime = startTime,
            endTime = endTime,
            weekDays = weekDays,
            isSavingPossible = false,
            timeFrameError = TimeFrameValidity.WeekDaysEmpty
        )
        val expectedUiState = testUiState.copy(timeFrameState = expectedTimeFrameState)
        assertEquals(expectedUiState, taskViewModel.uiState)
    }

    @Test
    fun changeTimeFrameState_InvalidState() = runTest {
        createTaskViewModel(navigateToSavedState = true)
        val startTime = LocalTime.of(10, 0)
        val endTime = LocalTime.of(11, 0)
        val weekDays = setOf<DayOfWeek>()
        val changeIntent = TaskIntent.ChangeTimeFrameState(newStartTime = startTime, newEndTime = endTime, newWeekDays = weekDays)
        assertFailsWith<IllegalStateException> { taskViewModel.handleIntent(changeIntent) }
    }

    @Test
    fun dismissTimeFrameState() {
        createTaskViewModel()
        val changeIntent = TaskIntent.ChangeTimeFrameState(newStartTime = LocalTime.now(), newEndTime = LocalTime.now(), newWeekDays = emptySet())
        taskViewModel.handleIntent(changeIntent) //to make sure a timeFrameState is actually present so we can check if it is dismissed
        taskViewModel.handleIntent(TaskIntent.DismissTimeFrameState)
        val expectedUiState = testUiState.copy(timeFrameState = null)
        assertEquals(expectedUiState, taskViewModel.uiState)
    }

    @Test
    fun saveCurrentTimeFrameState() = runTest {
        val newWeekDayTimeFrames =
            setOf(
                WeekDayTimeFrame(weekDay = DayOfWeek.MONDAY, timeFrame = TimeFrame(startTime = LocalTime.of(10, 0), endTime = LocalTime.of(11, 0))),
                WeekDayTimeFrame(weekDay = DayOfWeek.WEDNESDAY, timeFrame = TimeFrame(startTime = LocalTime.of(12, 0), endTime = LocalTime.of(13, 0)))
            )
        createTaskViewModel(weekDayTimeFramesToAdd = newWeekDayTimeFrames)

        val newTaskState = testUiState.taskState.copy(
            weekDayTimeFrames = testUiState.taskState.weekDayTimeFrames + newWeekDayTimeFrames,
            weekDayTimeFrameValidity = WeekDayTimeFrameValidity.Valid,
            isSavingPossible = true
        )
        val expectedUIState = testUiState.copy(taskState = newTaskState, timeFrameState = null)
        assertEquals(expectedUIState, taskViewModel.uiState)
    }

    @Test
    fun saveCurrentTimeFrameState_InvalidState() = runTest {
        createTaskViewModel(navigateToSavedState = true)
        val exception = assertFailsWith<IllegalStateException> { taskViewModel.handleIntent(TaskIntent.SaveCurrentTimeFrameState) }
        val expectedMessage = "ui state must be in EditTask state but was ${taskViewModel.uiState}"
        assertEquals(expectedMessage, exception.message)
    }

    @Test
    fun saveCurrentTimeFrameState_TimeFrameStateNull() = runTest {
        createTaskViewModel()
        val exception = assertFailsWith<IllegalStateException> { taskViewModel.handleIntent(TaskIntent.SaveCurrentTimeFrameState) }
        val expectedMessage = "can't save current time Frame when it is null"
        assertEquals(expectedMessage, exception.message)
    }

    @Test
    fun saveCurrentTimeFrameState_SavingNotPossible() = runTest {
        createTaskViewModel()
        val invalidChangeIntent =
            TaskIntent.ChangeTimeFrameState(newStartTime = LocalTime.MIDNIGHT, newEndTime = LocalTime.MIDNIGHT, newWeekDays = setOf())
        taskViewModel.handleIntent(invalidChangeIntent)
        val exception = assertFailsWith<IllegalStateException> { taskViewModel.handleIntent(TaskIntent.SaveCurrentTimeFrameState) }
        val expectedMessage = "saving current time frame is not allowed when saving is not possible"
        assertEquals(expectedMessage, exception.message)
    }

    @Test
    fun saveTask() = runTest {
        val taskToSave = testTask
        createTaskViewModel()
        val saveIntent = TaskIntent.SaveTask
        taskViewModel.handleIntent(saveIntent)
        advanceUntilIdle()

        val expectedUIState = TaskUiState.Saved
        assertEquals(expectedUIState, taskViewModel.uiState)
        val taskTimesFromDatabase = taskRepository.getWeekDayTimeFramesOfTask(taskToSave).sortedBy { it.weekDay }
        val expectedTimes = taskTimeEntities.filter { it.taskName == taskToSave }.map { it.toWeekDayTimeFrame() }.sortedBy { it.weekDay }
        assertEquals(expectedTimes, taskTimesFromDatabase)
    }

    @Test
    fun saveTask_InvalidState() = runTest {
        createTaskViewModel(navigateToSavedState = true)
        val saveIntent = TaskIntent.SaveTask
        val exception = assertFailsWith<IllegalStateException> { taskViewModel.handleIntent(saveIntent) }
        val expectedMessage = "ui state must be in EditTask state but was ${taskViewModel.uiState}"
        assertEquals(expectedMessage, exception.message)
    }

    @Test
    fun saveTask_SavingNotPossible() = runTest {
        createTaskViewModel(taskName = null)
        val saveIntent = TaskIntent.SaveTask
        val exception = assertFailsWith<IllegalStateException> { taskViewModel.handleIntent(saveIntent) }
        val expectedMessage = "Saving was tried despite the ui state stating it is not possible"
        assertEquals(expectedMessage, exception.message)
    }

}