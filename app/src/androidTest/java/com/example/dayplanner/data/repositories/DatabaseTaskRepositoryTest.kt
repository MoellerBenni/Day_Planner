package com.example.dayplanner.data.repositories

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.dayplanner.data.database.TaskDao
import com.example.dayplanner.data.database.entities.testTaskEntities
import com.example.dayplanner.data.database.entities.testTaskTimeEntities
import com.example.dayplanner.data.database.getPrePopulatedDatabase
import com.example.dayplanner.model.TimeFrame
import com.example.dayplanner.model.WeekDayTimeFrame
import kotlinx.coroutines.test.runTest
import org.junit.Before
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class DatabaseTaskRepositoryTest {
    private lateinit var context: Context
    private lateinit var taskDao: TaskDao
    private lateinit var taskRepository: DatabaseTaskRepository
    private val taskEntities = testTaskEntities
    private val taskTimeEntities = testTaskTimeEntities

    @Before
    fun createContext() = runTest {
        context = ApplicationProvider.getApplicationContext()
        taskDao = getPrePopulatedDatabase(context = context, taskEntities = taskEntities, taskTimeEntities = taskTimeEntities)
        taskRepository = DatabaseTaskRepository(taskDao)
    }

    @Test
    fun saveTask_InvalidTaskName() = runTest {
        val invalidTaskName = ""
        assertFailsWith<IllegalArgumentException> { taskRepository.saveTask(taskName = invalidTaskName, weekDayTimeFrames = emptySet()) }
    }

    @Test
    fun saveTask() = runTest {
        val taskName = "test"
        val weekDayTimeFrames = setOf(
            WeekDayTimeFrame(
                weekDay = DayOfWeek.MONDAY,
                timeFrame = TimeFrame(startTime = LocalTime.of(10, 0), endTime = LocalTime.of(11, 0))
            ), WeekDayTimeFrame(
                weekDay = DayOfWeek.SATURDAY,
                timeFrame = TimeFrame(startTime = LocalTime.of(10, 0), endTime = LocalTime.of(11, 0))
            )
        )
        taskRepository.saveTask(taskName = taskName, weekDayTimeFrames = weekDayTimeFrames)

        val timeFramesFromDatabase = taskRepository.getWeekDayTimeFramesOfTask(taskName).sortedBy { it.weekDay }.toSet()
        assertEquals(weekDayTimeFrames, timeFramesFromDatabase)
    }

    @Test
    fun saveTask_OverwriteOldTask() = runTest {
        val taskName = testTaskEntities.first().name
        val weekDayTimeFrames = setOf(
            WeekDayTimeFrame(
                weekDay = DayOfWeek.MONDAY,
                timeFrame = TimeFrame(startTime = LocalTime.of(10, 0), endTime = LocalTime.of(11, 0))
            ), WeekDayTimeFrame(
                weekDay = DayOfWeek.SATURDAY,
                timeFrame = TimeFrame(startTime = LocalTime.of(10, 0), endTime = LocalTime.of(11, 0))
            )
        )
        taskRepository.saveTask(taskName = taskName, weekDayTimeFrames = weekDayTimeFrames)

        val timeFramesFromDatabase = taskRepository.getWeekDayTimeFramesOfTask(taskName).sortedBy { it.weekDay }.toSet()
        assertEquals(weekDayTimeFrames, timeFramesFromDatabase)
    }

    @Test
    fun deleteTask() = runTest {
        val taskToDelete = testTaskEntities.first().name
        taskRepository.deleteTask(taskToDelete)
        val doesTaskExistInDatabase = taskRepository.doesTaskAlreadyExist(taskToDelete)
        assertFalse(doesTaskExistInDatabase)
    }

    @Test
    fun doesTaskAlreadyExists_True() = runTest {
        val task = testTaskEntities.first().name
        val doesTaskExistInDatabase = taskRepository.doesTaskAlreadyExist(task)
        assertTrue(doesTaskExistInDatabase)
    }

    @Test
    fun doesTaskAlreadyExists_False() = runTest {
        val task = "invalid task name"
        val doesTaskExistInDatabase = taskRepository.doesTaskAlreadyExist(task)
        assertFalse(doesTaskExistInDatabase)
    }

    @Test
    fun getWeekDayTimeFramesOfTask() = runTest {
        val task = testTaskEntities.first().name
        val weekDayTimeFramesFromDatabase = taskRepository.getWeekDayTimeFramesOfTask(task).sortedBy { it.weekDay }
        val expectedWeekDayTimeFrames = testTaskTimeEntities.filter { it.taskName == task }.map { it.toWeekDayTimeFrame() }.sortedBy { it.weekDay }
        assertEquals(expectedWeekDayTimeFrames, weekDayTimeFramesFromDatabase)
    }


}