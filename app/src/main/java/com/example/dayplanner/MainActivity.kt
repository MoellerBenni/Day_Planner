package com.example.dayplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.dayplanner.ui.screens.taskScreen.TaskScreen
import com.example.dayplanner.ui.screens.taskScreen.TaskViewModel
import com.example.dayplanner.ui.theme.DayPlannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DayPlannerTheme {
                val viewModel: TaskViewModel by viewModels { TaskViewModel.factory(taskName = "hallo") }
                TaskScreen(
                    uiState = viewModel.uiState,
                    onIntent = viewModel::handleIntent,
                    onNavigateBack = {},
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
