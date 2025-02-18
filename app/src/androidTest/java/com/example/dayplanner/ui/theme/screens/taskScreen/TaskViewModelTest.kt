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
            isSavingPossible = false
        ), timeFrameState = null
    )

    @Before
    fun createContext() {
        context = ApplicationProvider.getApplicationContext()
    }

    private fun createTaskViewModel(taskName: String? = testTask, navigateToSavedState: Boolean = false) = runTest {
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

        if (navigateToSavedState) {
            val uiState = taskViewModel.uiState as TaskUiState.EditTask
            val changeIntent =
                TaskIntent.ChangeWeekDayTimeFrames(newWeekDayTimeFrames = uiState.taskState.weekDayTimeFrames) //to make saving possible
            taskViewModel.handleIntent(changeIntent)
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
    fun changeWeekDayTimeFrames_Valid() = runTest {
        createTaskViewModel()
        val newWeekDayTimeFrames =
            setOf(
                WeekDayTimeFrame(weekDay = DayOfWeek.MONDAY, timeFrame = TimeFrame(startTime = LocalTime.of(10, 0), endTime = LocalTime.of(11, 0))),
                WeekDayTimeFrame(weekDay = DayOfWeek.WEDNESDAY, timeFrame = TimeFrame(startTime = LocalTime.of(12, 0), endTime = LocalTime.of(13, 0)))
            )
        val changeIntent = TaskIntent.ChangeWeekDayTimeFrames(newWeekDayTimeFrames)
        taskViewModel.handleIntent(changeIntent)
        val newTaskState = testUiState.taskState.copy(
            weekDayTimeFrames = newWeekDayTimeFrames,
            weekDayTimeFrameValidity = WeekDayTimeFrameValidity.Valid,
            isSavingPossible = true
        )
        val expectedUIState = testUiState.copy(taskState = newTaskState)
        assertEquals(expectedUIState, taskViewModel.uiState)
    }

    @Test
    fun changeWeekDayTimeFrames_Empty() = runTest {
        createTaskViewModel()
        val newWeekDayTimeFrames = setOf<WeekDayTimeFrame>()
        val changeIntent = TaskIntent.ChangeWeekDayTimeFrames(newWeekDayTimeFrames)
        taskViewModel.handleIntent(changeIntent)
        val newTaskState = testUiState.taskState.copy(
            weekDayTimeFrames = newWeekDayTimeFrames,
            weekDayTimeFrameValidity = WeekDayTimeFrameValidity.Empty,
            isSavingPossible = false
        )
        val expectedUIState = testUiState.copy(taskState = newTaskState)
        assertEquals(expectedUIState, taskViewModel.uiState)
    }

    @Test
    fun changeWeekDayTimeFrames_InvalidState() = runTest {
        createTaskViewModel(navigateToSavedState = true)
        val newWeekDayTimeFrames = setOf<WeekDayTimeFrame>()
        val changeIntent = TaskIntent.ChangeWeekDayTimeFrames(newWeekDayTimeFrames)
        assertFailsWith<IllegalStateException> { taskViewModel.handleIntent(changeIntent) }
    }

    @Test
    fun saveTask() = runTest {
        val taskToSave = testTask
        createTaskViewModel(taskName = taskToSave)
        val changeIntent = TaskIntent.ChangeWeekDayTimeFrames((taskViewModel.uiState as TaskUiState.EditTask).taskState.weekDayTimeFrames)
        taskViewModel.handleIntent(changeIntent) //saving is only possible in the ViewModel if something was altered first

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