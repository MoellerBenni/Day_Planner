package com.example.dayplanner.data.useCases

import androidx.lifecycle.ViewModel
import com.example.dayplanner.data.database.entities.TaskEntity
import com.example.dayplanner.data.repositories.TaskRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ValidateTaskNameUseCase(
    private val taskRepository: TaskRepository,
    coroutineScope: CoroutineScope,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private var taskNames: List<String> = emptyList()

    init {
        coroutineScope.launch {
            withContext(dispatcher) {
                taskRepository.getTaskNamesAsFlow().collect {
                    taskNames = it
                }
            }
        }
    }

    /**
     * checks whether [taskName] is a valid name for a task
     * @param taskName the name to be checked
     * @return the result as a [TaskNameValidationResult]
     */
    operator fun invoke(taskName: String): TaskNameValidationResult {
        return if (!TaskEntity.isValidTaskName(taskName)) {
            TaskNameValidationResult.InvalidName
        } else if (taskNames.contains(taskName)) {
            TaskNameValidationResult.NameAlreadyUsed
        } else {
            TaskNameValidationResult.Valid
        }
    }
}

sealed interface TaskNameValidationResult {
    data object Valid : TaskNameValidationResult
    data object InvalidName : TaskNameValidationResult
    data object NameAlreadyUsed : TaskNameValidationResult
}