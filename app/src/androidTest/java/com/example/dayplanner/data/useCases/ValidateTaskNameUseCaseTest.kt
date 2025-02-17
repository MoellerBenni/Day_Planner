package com.example.dayplanner.data.useCases

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.dayplanner.MainDispatcherRule
import com.example.dayplanner.data.database.entities.testTaskEntities
import com.example.dayplanner.data.database.entities.testTaskTimeEntities
import com.example.dayplanner.data.database.getPrePopulatedDatabase
import com.example.dayplanner.data.repositories.DatabaseTaskRepository
import com.example.dayplanner.data.repositories.TaskRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals


@OptIn(ExperimentalCoroutinesApi::class)
class ValidateTaskNameUseCaseTest {
    private lateinit var context: Context
    private lateinit var taskRepository: TaskRepository
    private lateinit var taskValidityUseCase: ValidateTaskNameUseCase
    private val taskEntities = testTaskEntities
    private val taskTimeEntities = testTaskTimeEntities

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun createContext() = runTest {
        context = ApplicationProvider.getApplicationContext()
        val taskDao = getPrePopulatedDatabase(
            context = context,
            taskEntities = taskEntities,
            taskTimeEntities = taskTimeEntities,
            executor = mainDispatcherRule.testDispatcher.asExecutor()
        )
        taskRepository = DatabaseTaskRepository(taskDao = taskDao)
        taskValidityUseCase =
            ValidateTaskNameUseCase(taskRepository, coroutineScope = this.backgroundScope, dispatcher = mainDispatcherRule.testDispatcher)
        runCurrent()
    }

    @Test
    fun validaTaskName_Valid() = runTest {
        val taskName = "valid task name"
        val result = taskValidityUseCase(taskName)
        val expectedResult = TaskNameValidationResult.Valid
        assertEquals(expectedResult, result)
    }

    @Test
    fun validaTaskName_Invalid() = runTest {
        val taskName = ""
        val result = taskValidityUseCase(taskName)
        val expectedResult = TaskNameValidationResult.InvalidName
        assertEquals(expectedResult, result)
    }

    @Test
    fun validaTaskName_NameAlreadyUsed() = runTest {
        val taskName = taskEntities.first().name
        val result = taskValidityUseCase(taskName)
        val expectedResult = TaskNameValidationResult.NameAlreadyUsed
        assertEquals(expectedResult, result)
    }
}