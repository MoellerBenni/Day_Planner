package com.example.dayplanner.ui.general

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertTrue

class IconAndTextKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private val testIcon = Icons.Outlined.DateRange
    private val testText = "test text"

    private fun showTextAndIcon(
        imageVector: ImageVector = testIcon,
        text: String = testText,
        onClick: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            IconAndText(imageVector = imageVector, text = text, onClick = onClick)
        }
    }

    @Test
    fun onClick() {
        var wasClicked = false
        showTextAndIcon(onClick = {wasClicked = true})
        composeTestRule.onNodeWithText(testText).performClick()
        assertTrue(wasClicked)
    }
}