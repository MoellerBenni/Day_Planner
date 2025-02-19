package com.example.dayplanner.ui.general

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextReplacement
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


class TaskNameTextFieldKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private val testName = "test Name"
    private val testTag = "test tag"

    private fun showTextField(
        text: String = testName,
        onTextChanged: (String) -> Unit = {},
        errorMessage: String? = null,
        readOnly: Boolean = false
    ) {
        composeTestRule.setContent {
            TaskNameTextField(
                text = text,
                onTextChanged = onTextChanged,
                errorMessage = errorMessage,
                readOnly = readOnly,
                modifier = Modifier.testTag(testTag)
            )
        }
    }

    @Test
    fun textDisplayed() {
        showTextField()
        composeTestRule.onNodeWithText(testName).assertIsDisplayed()
    }

    @Test
    fun textChanged() {
        val textToType = "new text"
        var typedText: String? = null
        showTextField(onTextChanged = { typedText = it })
        composeTestRule.onNodeWithTag(testTag).performTextReplacement(textToType)
        assertEquals(textToType, typedText)
    }

    @Test
    fun textChanged_ReadOnly() {
        val textToType = "new text"
        var typedText: String? = null
        showTextField(onTextChanged = { typedText = it }, readOnly = true)
        composeTestRule.onNodeWithTag(testTag).performTextReplacement(textToType)
        assertNull(typedText)
    }

    @Test
    fun errorMessageDisplayed() {
        val errorMessage = "test error message"
        showTextField(errorMessage = errorMessage)
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }


}

