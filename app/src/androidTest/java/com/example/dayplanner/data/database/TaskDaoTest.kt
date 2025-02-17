package com.example.dayplanner.data.database

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.test.core.app.ApplicationProvider
import com.example.dayplanner.data.database.entities.TaskEntity
import com.example.dayplanner.data.database.entities.TaskTimeEntity
import com.example.dayplanner.data.database.entities.testTaskEntities
import com.example.dayplanner.data.database.entities.testTaskTimeEntities
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TaskDaoTest {

    private lateinit var taskDao: TaskDao
    private val taskEntities = testTaskEntities
    private val taskTimeEntities = testTaskTimeEntities

    @Before
    fun createDao() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        taskDao = getPrePopulatedDatabase(context = context, taskEntities = taskEntities, taskTimeEntities = taskTimeEntities)
    }

    @Test
    fun saveTaskEntity() = runTest {
        val newTask = TaskEntity(name = "test Task")
        taskDao.saveTaskEntity(newTask)
        val taskFromDatabase = taskDao.getTaskEntityByName(newTask.name)
        assertEquals(newTask, taskFromDatabase)
    }

    @Test
    fun saveTaskEntity_AlreadySaved() = runTest {
        val newTask = testTaskEntities.first()
        assertFailsWith<SQLiteConstraintException> { taskDao.saveTaskEntity(newTask) }
    }

    @Test
    fun deleteTaskEntity() = runTest {
        val taskToDelete = testTaskEntities.first()
        taskDao.deleteTaskEntity(taskToDelete)
        val taskFromDatabase = taskDao.getTaskEntityByName(taskToDelete.name)
        assertNull(taskFromDatabase)

        //now check if TaskTimeEntities are deleted as well
        val taskTimesFromDatabase = taskDao.getTaskTimesOfTaskEntity(taskToDelete.name)
        assertTrue(taskTimesFromDatabase.isEmpty())
    }

    @Test
    fun getTaskEntityByName_Found() = runTest {
        val taskToGet = testTaskEntities.first()
        val taskFromDatabase = taskDao.getTaskEntityByName(taskToGet.name)
        assertEquals(taskToGet, taskFromDatabase)
    }

    @Test
    fun getTaskEntityByName_NOtFound() = runTest {
        val invalidTaskName = "invalid"
        val taskFromDatabase = taskDao.getTaskEntityByName(invalidTaskName)
        assertNull(taskFromDatabase)
    }

    @Test
    fun saveTaskTimeEntity() = runTest {
        val taskName = testTaskEntities.first().name
        val newTaskTime = TaskTimeEntity(
            taskName = taskName,
            weekDay = DayOfWeek.SUNDAY,
            startTime = LocalTime.of(11, 50),
            endTime = LocalTime.of(12, 45)
        )
        taskDao.saveTaskTimeEntity(newTaskTime)
        val taskTimeEntitiesFromDatabase = taskDao.getTaskTimesOfTaskEntity(taskName = taskName).sortedBy { it.weekDay }
        val expectedTaskTimeEntities = (testTaskTimeEntities.filter { it.taskName == taskName } + newTaskTime).sortedBy { it.weekDay }
        assertEquals(expectedTaskTimeEntities, taskTimeEntitiesFromDatabase)
    }

    @Test
    fun getTaskTimesOfTaskEntity() = runTest {
        val taskName = testTaskEntities.first().name
        val taskTimesFromDatabase = taskDao.getTaskTimesOfTaskEntity(taskName).sortedBy { it.weekDay }
        val expectedTaskTimeEntities = testTaskTimeEntities.filter { it.taskName == taskName }.sortedBy { it.weekDay }
        assertEquals(expectedTaskTimeEntities, taskTimesFromDatabase)
    }

    @Test
    fun getTaskTimesOfTaskEntity_TaskEntityNotFound() = runTest {
        val taskName = "invalid"
        val taskTimesFromDatabase = taskDao.getTaskTimesOfTaskEntity(taskName)
        assertTrue(taskTimesFromDatabase.isEmpty())
    }


}