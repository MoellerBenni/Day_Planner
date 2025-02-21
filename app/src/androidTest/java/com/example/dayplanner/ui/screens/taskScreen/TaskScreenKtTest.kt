package com.example.dayplanner.ui.screens.taskScreen

import android.content.Context
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ApplicationProvider
import com.example.dayplanner.R
import com.example.dayplanner.data.database.entities.testTaskTimeEntities
import com.example.dayplanner.data.useCases.TaskNameValidationResult
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TaskScreenKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var context: Context
    private var receivedIntent: TaskIntent? = null
    private val weekDayTimeFrames = testTaskTimeEntities.map { it.toWeekDayTimeFrame() }.toSet()
    private val taskState = TaskState(
        taskName = "test task name",
        taskNameValidity = TaskNameValidationResult.InvalidName,
        isTaskNameEditable = true,
        weekDayTimeFrames = weekDayTimeFrames,
        weekDayTimeFrameValidity = WeekDayTimeFrameValidity.Empty,
        isSavingPossible = true
    )
    private val testUiState = TaskUiState.EditTask(taskState = taskState, timeFrameState = null)
    private val testTimeFrameState = TimeFrameState(
        startTime = LocalTime.MIDNIGHT,
        endTime = LocalTime.MIDNIGHT,
        weekDays = setOf(DayOfWeek.MONDAY),
        isSavingPossible = true,
        timeFrameError = TimeFrameValidity.Valid
    )

    @Before
    fun createContext() {
        context = ApplicationProvider.getApplicationContext()
        receivedIntent = null
    }

    private fun showTaskScreen(uiState: TaskUiState = testUiState, onNavigateBack: () -> Unit = {}) {
        composeTestRule.setContent {
            TaskScreen(uiState = uiState, onIntent = { receivedIntent = it }, onNavigateBack)
        }
    }

    @Test
    fun taskScreen_LoadingTask() {
        val uiState = TaskUiState.Loading
        showTaskScreen(uiState = uiState)
        composeTestRule.onNodeWithText(context.getString(R.string.loading_task)).assertIsDisplayed()
    }

    @Test
    fun taskScreen_TaskNotFound() {
        var wasNavigatedBack = false
        val uiState = TaskUiState.TaskNotFound
        showTaskScreen(uiState = uiState, onNavigateBack = { wasNavigatedBack = true })
        composeTestRule.onNodeWithText(context.getString(R.string.task_was_not_found)).assertIsDisplayed()
        composeTestRule.mainClock.advanceTimeBy(500)
        assertTrue(wasNavigatedBack)
    }

    @Test
    fun taskScreen_SavingTask() {
        val uiState = TaskUiState.Saving
        showTaskScreen(uiState = uiState)
        composeTestRule.onNodeWithText(context.getString(R.string.saving_task)).assertIsDisplayed()
    }

    @Test
    fun taskScreen_TaskSaved() {
        var wasNavigatedBack = false
        val uiState = TaskUiState.Saved
        showTaskScreen(uiState = uiState, onNavigateBack = { wasNavigatedBack = true })
        composeTestRule.onNodeWithText(context.getString(R.string.task_was_saved)).assertIsDisplayed()
        composeTestRule.mainClock.advanceTimeBy(500)
        assertTrue(wasNavigatedBack)
    }

    @Test
    fun taskNameDisplayed() {
        showTaskScreen()
        composeTestRule.onNodeWithText(testUiState.taskState.taskName).assertIsDisplayed()
    }

    @Test
    fun taskNameChanged() {
        val newText = "new Text"
        showTaskScreen()
        composeTestRule.onNodeWithText(testUiState.taskState.taskName).performTextReplacement(newText)
        val expectedIntent = TaskIntent.ChangeTaskName(newText)
        assertEquals(expectedIntent, receivedIntent)
    }

    @Test
    fun taskTextFieldReadyOnly_True() {
        val taskState = testUiState.taskState.copy(isTaskNameEditable = true)
        showTaskScreen(uiState = testUiState.copy(taskState = taskState))
        val isEditable = composeTestRule.onNodeWithText(testUiState.taskState.taskName).fetchSemanticsNode().config[(SemanticsProperties.IsEditable)]
        assertTrue(isEditable)
    }

    @Test
    fun taskTextFieldReadyOnly_False() {
        val taskState = testUiState.taskState.copy(isTaskNameEditable = false)
        showTaskScreen(uiState = testUiState.copy(taskState = taskState))
        val isEditable = composeTestRule.onNodeWithText(testUiState.taskState.taskName).fetchSemanticsNode().config[(SemanticsProperties.IsEditable)]
        assertFalse(isEditable)
    }

    @Test
    fun taskTextField_InvalidName() {
        val errorMessage = context.getString(R.string.task_name_is_invalid)
        val taskState = testUiState.taskState.copy(taskNameValidity = TaskNameValidationResult.InvalidName)
        showTaskScreen(uiState = testUiState.copy(taskState = taskState))
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun taskTextField_NameAlreadyUsed() {
        val errorMessage = context.getString(R.string.task_name_is_already_used)
        val taskState = testUiState.taskState.copy(taskNameValidity = TaskNameValidationResult.NameAlreadyUsed)
        showTaskScreen(uiState = testUiState.copy(taskState = taskState))
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun weekDayTimeFramesDisplayed() {
        showTaskScreen()
        for (weekDayTimeFrame in weekDayTimeFrames) {
            val timeFrameItem = composeTestRule.onNodeWithTag("weekDayTimeFrame: $weekDayTimeFrame")
            timeFrameItem.performScrollTo()
            timeFrameItem.assertIsDisplayed()
        }
    }

    @Test
    fun weekDayTimeFrames_DeleteClicked() {
        showTaskScreen()
        for (weekDayTimeFrame in weekDayTimeFrames) {
            composeTestRule.onNodeWithContentDescription("delete $weekDayTimeFrame").performClick()
            val expectedIntent = TaskIntent.DeleteWeekDayTimeFrames(setOf(weekDayTimeFrame))
            assertEquals(expectedIntent, receivedIntent)
        }
    }

    @Test
    fun addTimeFrameDisplayed() {
        val taskState = testUiState.taskState.copy(weekDayTimeFrames = emptySet())
        showTaskScreen(uiState = testUiState.copy(taskState = taskState))
        composeTestRule.onNodeWithText(context.getString(R.string.add_timeframes)).assertIsDisplayed()
    }

    @Test
    fun weekDayTimeFrameError() {
        val errorMessage = context.getString(R.string.time_frames_must_not_be_empty)
        val taskState = testUiState.taskState.copy(weekDayTimeFrameValidity = WeekDayTimeFrameValidity.Empty)
        showTaskScreen(uiState = testUiState.copy(taskState = taskState))
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun removeAllWeekDayTimeFramesClicked() {
        showTaskScreen()
        composeTestRule.onNodeWithText(context.getString(R.string.remove_all)).performClick()
        val expectedIntent = TaskIntent.DeleteWeekDayTimeFrames(weekDayTimeFrames)
        assertEquals(expectedIntent, receivedIntent)
    }

    @Test
    fun addNewTimeFramesClicked() {
        showTaskScreen()
        composeTestRule.onNodeWithText(context.getString(R.string.add_new_time_frames)).performClick()
        val expectedIntent =
            TaskIntent.ChangeTimeFrameState(newStartTime = LocalTime.MIDNIGHT, newEndTime = LocalTime.MIDNIGHT, newWeekDays = emptySet())
        assertEquals(expectedIntent, receivedIntent)
    }

    @Test
    fun cancelClicked() {
        var wasNavigatedBack = false
        showTaskScreen(onNavigateBack = { wasNavigatedBack = true })
        composeTestRule.onNodeWithText(context.getString(R.string.cancel)).performClick()
        assertTrue(wasNavigatedBack)
    }

    @Test
    fun saveTaskButtonEnabled() {
        val taskState = testUiState.taskState.copy(isSavingPossible = true)
        showTaskScreen(uiState = testUiState.copy(taskState = taskState))
        composeTestRule.onNodeWithText(context.getString(R.string.save_task)).assertIsEnabled()
    }

    @Test
    fun saveTaskButtonNotEnabled() {
        val taskState = testUiState.taskState.copy(isSavingPossible = false)
        showTaskScreen(uiState = testUiState.copy(taskState = taskState))
        composeTestRule.onNodeWithText(context.getString(R.string.save_task)).assertIsNotEnabled()
    }

    @Test
    fun saveTaskButtonClicked() {
        val taskState = testUiState.taskState.copy(isSavingPossible = true)
        showTaskScreen(uiState = testUiState.copy(taskState = taskState))
        composeTestRule.onNodeWithText(context.getString(R.string.save_task)).performClick()
        val expectedIntent = TaskIntent.SaveTask
        assertEquals(expectedIntent, receivedIntent)
    }

    @Test
    fun timeFrameDialogDisplayed() {
        showTaskScreen(uiState = testUiState.copy(timeFrameState = testTimeFrameState))
        composeTestRule.onNodeWithText(context.getString(R.string.pick_time_frames)).assertIsDisplayed()
    }

    @Test
    fun timeFrameDialog_TimeFramesChanged() {
        showTaskScreen(uiState = testUiState.copy(timeFrameState = testTimeFrameState))
        val newStartTime = LocalTime.of(6, 30)
        val newEndTime = LocalTime.of(11, 45)
        composeTestRule.onAllNodesWithContentDescription("for hour")[0].performTextReplacement(newStartTime.hour.toString())
        composeTestRule.waitForIdle()
        var expectedIntent =
            TaskIntent.ChangeTimeFrameState(
                newStartTime = LocalTime.of(newStartTime.hour, 0),
                newEndTime = LocalTime.MIDNIGHT,
                newWeekDays = testTimeFrameState.weekDays
            )
        assertEquals(expectedIntent, receivedIntent)

        composeTestRule.onAllNodesWithContentDescription("for minutes")[0].performTextReplacement(newStartTime.minute.toString())
        composeTestRule.waitForIdle()
        expectedIntent =
            TaskIntent.ChangeTimeFrameState(
                newStartTime = newStartTime,
                newEndTime = LocalTime.MIDNIGHT,
                newWeekDays = testTimeFrameState.weekDays
            )
        assertEquals(expectedIntent, receivedIntent)

        composeTestRule.onAllNodesWithContentDescription("for hour")[1].performTextReplacement(newEndTime.hour.toString())
        composeTestRule.waitForIdle()
        expectedIntent =
            TaskIntent.ChangeTimeFrameState(
                newStartTime = newStartTime,
                newEndTime = LocalTime.of(newEndTime.hour, 0),
                newWeekDays = testTimeFrameState.weekDays
            )
        assertEquals(expectedIntent, receivedIntent)

        composeTestRule.onAllNodesWithContentDescription("for minutes")[1].performTextReplacement(newEndTime.minute.toString())
        composeTestRule.waitForIdle()
        expectedIntent =
            TaskIntent.ChangeTimeFrameState(
                newStartTime = newStartTime,
                newEndTime = newEndTime,
                newWeekDays = testTimeFrameState.weekDays
            )
        assertEquals(expectedIntent, receivedIntent)
    }

    @Test
    fun timeFrameDialog_WeekDaysSelected() {
        val selectedWeekDays = testTimeFrameState.weekDays
        showTaskScreen(uiState = testUiState.copy(timeFrameState = testTimeFrameState))
        for (weekDay in DayOfWeek.entries) {
            val weekDaySelector = composeTestRule.onNodeWithTag("$weekDay selector")
            if (weekDay in selectedWeekDays) weekDaySelector.assertIsOn() else weekDaySelector.assertIsOff()
        }
    }

    @Test
    fun timeFrameDialog_WeekDaysClicked() {
        val selectedWeekDays = testTimeFrameState.weekDays
        showTaskScreen(uiState = testUiState.copy(timeFrameState = testTimeFrameState))
        for (weekDay in DayOfWeek.entries) {
            composeTestRule.onNodeWithTag("$weekDay selector").performClick()
            val newWeekDays = if (weekDay in selectedWeekDays) selectedWeekDays - weekDay else selectedWeekDays + weekDay
            val expectedIntent =
                TaskIntent.ChangeTimeFrameState(newStartTime = LocalTime.MIDNIGHT, newEndTime = LocalTime.MIDNIGHT, newWeekDays = newWeekDays)
            assertEquals(expectedIntent, receivedIntent)
        }
    }

    @Test
    fun timeFrameDialog_ErrorMessage_StartTimeNotBeforeEndTime() {
        val errorMessage = context.getString(R.string.start_time_must_be_before_end_time)
        val timeFrameState = testTimeFrameState.copy(timeFrameError = TimeFrameValidity.StartTimeNotBeforeEndTime)
        showTaskScreen(uiState = testUiState.copy(timeFrameState = timeFrameState))
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun timeFrameDialog_ErrorMessage_WeekDaysEmpty() {
        val errorMessage = context.getString(R.string.no_week_days_are_selected)
        val timeFrameState = testTimeFrameState.copy(timeFrameError = TimeFrameValidity.WeekDaysEmpty)
        showTaskScreen(uiState = testUiState.copy(timeFrameState = timeFrameState))
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun timeFrameDialog_OnCancelClicked() {
        showTaskScreen(uiState = testUiState.copy(timeFrameState = testTimeFrameState))
        composeTestRule.onAllNodesWithText(context.getString(R.string.cancel))[1].performClick()
        val expectedIntent = TaskIntent.DismissTimeFrameState
        assertEquals(expectedIntent, receivedIntent)
    }

    @Test
    fun timeFrameDialog_ConfirmButtonEnabled() {
        val timeFrameState = testTimeFrameState.copy(isSavingPossible = true)
        showTaskScreen(uiState = testUiState.copy(timeFrameState = timeFrameState))
        composeTestRule.onNodeWithText(context.getString(R.string.confirm)).assertIsEnabled()
    }

    @Test
    fun timeFrameDialog_ConfirmButtonNotEnabled() {
        val timeFrameState = testTimeFrameState.copy(isSavingPossible = false)
        showTaskScreen(uiState = testUiState.copy(timeFrameState = timeFrameState))
        composeTestRule.onNodeWithText(context.getString(R.string.confirm)).assertIsNotEnabled()
    }

    @Test
    fun timeFrameDialog_ConfirmButtonClicked() {
        val timeFrameState = testTimeFrameState.copy(isSavingPossible = true)
        showTaskScreen(uiState = testUiState.copy(timeFrameState = timeFrameState))
        composeTestRule.onNodeWithText(context.getString(R.string.confirm)).performClick()
        val expectedIntent = TaskIntent.SaveCurrentTimeFrameState
        assertEquals(expectedIntent, receivedIntent)
    }


}