package com.example.dayplanner.ui.general

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test


class ErrorTextKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private val initialText = "initial text"
    private val testTag = "test Tag"
    private lateinit var text: MutableState<String?>

    @Before
    fun showErrorText() {
        composeTestRule.setContent {
            text = remember { mutableStateOf(initialText) }
            ErrorText(text.value, modifier = Modifier.testTag(testTag))
        }
    }

    @Test
    fun textDisplayedAtStart() {
        composeTestRule.onNodeWithText(initialText).assertIsDisplayed()
    }

    @Test
    fun textVanishesWhenSetToNull() {
        text.value = null
        composeTestRule.onNodeWithTag("warning icon").assertDoesNotExist()
    }

    @Test
    fun textChangesName() {
        val newText = "new Text"
        text.value = newText
        composeTestRule.onNodeWithText(newText).assertIsDisplayed()
    }
}