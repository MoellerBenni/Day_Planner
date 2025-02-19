package com.example.dayplanner.ui.general

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.example.dayplanner.data.database.entities.testTaskTimeEntities
import com.example.dayplanner.model.WeekDayTimeFrame
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertTrue


class WeekDayTimeFrameItemKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    private val testWeekDayTimeFrame = testTaskTimeEntities.first().toWeekDayTimeFrame()

    private fun showTimeFrame(weekDayTimeFrame: WeekDayTimeFrame = testWeekDayTimeFrame, onDeleteClicked: () -> Unit = {}) {
        composeTestRule.setContent {
            WeekDayTimeFrameItem(weekDayTimeFrame = weekDayTimeFrame, onDeleteClicked = onDeleteClicked)
        }
    }

    @Test
    fun deleteItemClicked() {
        var wasClicked = false
        showTimeFrame(onDeleteClicked = { wasClicked = true })
        composeTestRule.onNodeWithContentDescription("delete $testWeekDayTimeFrame").performClick()
        assertTrue(wasClicked)
    }
}