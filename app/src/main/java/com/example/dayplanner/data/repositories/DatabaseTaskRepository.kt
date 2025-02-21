package com.example.dayplanner.data.repositories

import com.example.dayplanner.data.database.TaskDao
import com.example.dayplanner.data.database.entities.TaskEntity
import com.example.dayplanner.data.database.entities.TaskEntity.Companion.isValidTaskName
import com.example.dayplanner.data.database.entities.TaskTimeEntity
import com.example.dayplanner.model.TimeFrame
import com.example.dayplanner.model.WeekDayTimeFrame
import kotlinx.coroutines.flow.Flow

class DatabaseTaskRepository(private val taskDao: TaskDao) : TaskRepository {

    override suspend fun saveTask(taskName: String, weekDayTimeFrames: Set<WeekDayTimeFrame>) {
        require(isValidTaskName(taskName)) { "name: $taskName is not valid for a Task" }
        val taskEntity = TaskEntity(name = taskName)
        taskDao.deleteTaskEntity(taskEntity)
        val taskTimeEntities = weekDayTimeFrames.map {
            TaskTimeEntity(
                taskName = taskName,
                weekDay = it.weekDay,
                startTime = it.timeFrame.startTime,
                endTime = it.timeFrame.endTime
            )
        }

        taskDao.saveTaskEntity(taskEntity) //since task Entity was deleted before, saving can't fail
        for (taskTime in taskTimeEntities) {
            taskDao.saveTaskTimeEntity(taskTime) //since weekDayTimeFrames is a set, there are no duplicates so saving can't fail
        }

    }

    override suspend fun deleteTask(taskName: String) {
        val taskEntity = TaskEntity(name = taskName)
        taskDao.deleteTaskEntity(taskEntity)
    }

    override suspend fun doesTaskAlreadyExist(taskName: String): Boolean {
        val taskFromDatabase = taskDao.getTaskEntityByName(taskName)
        return taskFromDatabase != null
    }

    override suspend fun getWeekDayTimeFramesOfTask(taskName: String): List<WeekDayTimeFrame> {
        val taskTimeEntities = taskDao.getTaskTimesOfTaskEntity(taskName)
        return taskTimeEntities.map {
            val timeFrame = TimeFrame(startTime = it.startTime, endTime = it.endTime)
            WeekDayTimeFrame(weekDay = it.weekDay, timeFrame = timeFrame)
        }
    }

    override fun getTaskNamesAsFlow(): Flow<List<String>> = taskDao.getTaskNamesAsFlow()
}