package com.example.dayplanner.data.repositories

import com.example.dayplanner.model.WeekDayTimeFrame

interface TaskRepository {

    /**
     * saves a task to the database
     * @param taskName the name of the task
     * @param weekDayTimeFrames the [WeekDayTimeFrame] the task is planned on
     * @throws IllegalArgumentException when [taskName] is invalid
     */
    suspend fun saveTask(taskName: String, weekDayTimeFrames: Set<WeekDayTimeFrame>)

    /**
     * delete a Task with [taskName] from the database
     */
    suspend fun deleteTask(taskName: String)

    /**
     * checks if a Task with [taskName] already exists
     */
    suspend fun doesTaskAlreadyExist(taskName: String): Boolean

    /**
     * retrieves all [WeekDayTimeFrame] of a task with [taskName]
     */
    suspend fun getWeekDayTimeFramesOfTask(taskName: String): List<WeekDayTimeFrame>
}