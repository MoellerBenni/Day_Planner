package com.example.dayplanner.ui.general

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ApplicationProvider
import com.example.dayplanner.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class WeekDayTimeFrameDialogKtTest {

    private lateinit var context: Context

    @get:Rule
    val composeTestRule = createComposeRule()
    private val testWeekDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)

    @Before
    fun createContext() {
        context = ApplicationProvider.getApplicationContext()
    }

    private fun showDialog(
        selectedWeekDays: Set<DayOfWeek> = testWeekDays,
        onValueChanged: (StartTime, EndTime, Set<DayOfWeek>) -> Unit = { _, _, _ -> },
        onDismissRequest: () -> Unit = {},
        onConfirm: () -> Unit = {},
        confirmEnabled: Boolean = true,
        errorMessage: String? = null
    ) {
        composeTestRule.setContent {
            WeekDayTimeFrameDialog(
                selectedWeekDays = selectedWeekDays,
                onValueChanged = onValueChanged,
                onDismissRequest = onDismissRequest,
                onConfirm = onConfirm,
                confirmEnabled = confirmEnabled,
                errorMessage = errorMessage
            )
        }
    }

    @Test
    fun weekDaysSelected() {
        showDialog()
        for (weekDay in DayOfWeek.entries) {
            val weekDaySelector = composeTestRule.onNodeWithTag("$weekDay selector")
            if (weekDay in testWeekDays) weekDaySelector.assertIsOn() else weekDaySelector.assertIsOff()
        }
    }

    @Test
    fun weekDayClicked() {
        var changedValue: Triple<LocalTime, LocalTime, Set<DayOfWeek>>? = null
        showDialog(onValueChanged = { startTime, endTime, weekDays -> changedValue = Triple(startTime, endTime, weekDays) })

        for (weekDay in DayOfWeek.entries) {
            val weekDaySelector = composeTestRule.onNodeWithTag("$weekDay selector")
            val newWeekDays = if (weekDay in testWeekDays) testWeekDays - weekDay else testWeekDays + weekDay
            weekDaySelector.performClick()
            val expectedChangedValue = Triple(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT, newWeekDays)
            assertEquals(expectedChangedValue, changedValue)
        }
    }

    @Test
    fun timesChanged() {
        var changedValue: Triple<LocalTime, LocalTime, Set<DayOfWeek>>? = null
        showDialog(onValueChanged = { startTime, endTime, weekDays -> changedValue = Triple(startTime, endTime, weekDays) })
        val newStartTime = LocalTime.of(10, 35)
        val newEndTime = LocalTime.of(11, 40)


        composeTestRule.onAllNodesWithContentDescription("for hour")[0].performTextReplacement(newStartTime.hour.toString())
        composeTestRule.onAllNodesWithContentDescription("for minutes")[0].performTextReplacement(newStartTime.minute.toString())
        composeTestRule.onAllNodesWithContentDescription("for hour")[1].performTextReplacement(newEndTime.hour.toString())
        composeTestRule.onAllNodesWithContentDescription("for minutes")[1].performTextReplacement(newEndTime.minute.toString())
        composeTestRule.waitForIdle() //time picker need some time to settle

        val expectedChangedValue = Triple(newStartTime, newEndTime, testWeekDays)
        assertEquals(expectedChangedValue, changedValue)
    }

    @Test
    fun dialogDismissed() {
        var isDismissed = false
        showDialog(onDismissRequest = { isDismissed = true })
        composeTestRule.onNodeWithText(context.getString(R.string.cancel)).performClick()
        assertTrue(isDismissed)
    }

    @Test
    fun confirmButtonEnabled_True() {
        showDialog(confirmEnabled = true)
        composeTestRule.onNodeWithText(context.getString(R.string.confirm)).assertIsEnabled()
    }

    @Test
    fun confirmButtonEnabled_False() {
        showDialog(confirmEnabled = false)
        composeTestRule.onNodeWithText(context.getString(R.string.confirm)).assertIsNotEnabled()
    }

    @Test
    fun confirmButtonClicked() {
        var isClicked = false
        showDialog(confirmEnabled = true, onConfirm = { isClicked = true })
        composeTestRule.onNodeWithText(context.getString(R.string.confirm)).performClick()
        assertTrue(isClicked)
    }

    @Test
    fun errorMessageDisplayed() {
        val errorMessage = "test error message"
        showDialog(errorMessage = errorMessage)
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }


}