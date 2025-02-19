package com.example.dayplanner.ui.general

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class WeekDaySelectorKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private val testTag = "test tag"

    private fun showWeekDaySelector(weekDay: DayOfWeek = DayOfWeek.MONDAY, selected: Boolean = true, onClick: (Boolean) -> Unit = {}) {
        composeTestRule.setContent {
            WeekDaySelector(weekDay = weekDay, selected = selected, onClick = onClick, modifier = Modifier.testTag(testTag))
        }
    }

    @Test
    fun isSelected_True() {
        showWeekDaySelector(selected = true)
        composeTestRule.onNodeWithTag(testTag).assertIsOn()
    }

    @Test
    fun isSelected_False() {
        showWeekDaySelector(selected = false)
        composeTestRule.onNodeWithTag(testTag).assertIsOff()
    }

    @Test
    fun click_Selected() {
        var isSelected: Boolean? = null
        showWeekDaySelector(selected = true, onClick = {isSelected = it})
        composeTestRule.onNodeWithTag(testTag).performClick()
        assertFalse(isSelected!!)
    }

    @Test
    fun click_NotSelected() {
        var isSelected: Boolean? = null
        showWeekDaySelector(selected = false, onClick = {isSelected = it})
        composeTestRule.onNodeWithTag(testTag).performClick()
        assertTrue(isSelected!!)
    }
}