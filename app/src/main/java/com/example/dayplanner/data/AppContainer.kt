package com.example.dayplanner.data

import android.content.Context
import com.example.dayplanner.data.database.TaskDatabase
import com.example.dayplanner.data.repositories.DatabaseTaskRepository
import com.example.dayplanner.data.repositories.TaskRepository
import com.example.dayplanner.data.useCases.ValidateTaskNameUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * container that contains all dependencies for the app
 */
interface AppContainer {
    val taskRepository: TaskRepository
    val validateTaskNameUseCase: ValidateTaskNameUseCase
}

/**
 * implementation of [AppContainer] that contains all default dependencies
 */
class DefaultAppContainer(context: Context): AppContainer {
    override val taskRepository: TaskRepository
    override val validateTaskNameUseCase: ValidateTaskNameUseCase

    init {
        val database = TaskDatabase.getDatabase(context)
        taskRepository = DatabaseTaskRepository(database.dao())
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        validateTaskNameUseCase = ValidateTaskNameUseCase(taskRepository = taskRepository, coroutineScope = coroutineScope)
    }
}

