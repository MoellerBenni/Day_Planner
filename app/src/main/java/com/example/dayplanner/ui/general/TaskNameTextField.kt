package com.example.dayplanner.ui.general

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dayplanner.R
import com.example.dayplanner.ui.theme.DayPlannerTheme

/**
 * TextField that displays the name of a task
 * @param text the name to be displayed
 * @param onTextChanged called when the text is changed
 * @param modifier the [Modifier] for this Composable
 * @param errorMessage the error message to be displayed under the TextField, null if no error is present
 * @param readOnly whether the TextField can be edited
 */
@Composable
fun TaskNameTextField(
    text: String,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    readOnly: Boolean = false
) {
    TextField(modifier = modifier,
        value = text,
        onValueChange = onTextChanged,
        textStyle = MaterialTheme.typography.titleLarge,
        isError = errorMessage != null,
        readOnly = readOnly, singleLine = true,
        label = {
            Text(text = stringResource(R.string.task_name))
        },
        trailingIcon = {
            if (!readOnly) {
                IconButton(onClick = {  onTextChanged("") }) {
                    Icon(imageVector = Icons.Outlined.Clear, contentDescription = stringResource(R.string.clear_task_name))
                }
            }
        },
        supportingText = {
            Box(modifier = Modifier.height(25.dp)) {
                ErrorText(text = errorMessage)
            }
        })
}

@Preview(showBackground = true)
@Composable
private fun TaskNameTextField_Preview() {
    DayPlannerTheme {
        Column {
            val modifier = Modifier.padding(16.dp)
            TaskNameTextField(text = "", onTextChanged = {}, modifier = modifier)
            TaskNameTextField(text = "Invalid Task Name", onTextChanged = {}, errorMessage = "This Task Name is invalid", modifier = modifier)
        }
    }
}