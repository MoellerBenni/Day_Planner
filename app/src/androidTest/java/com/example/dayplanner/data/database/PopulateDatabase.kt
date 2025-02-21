package com.example.dayplanner.data.database

import android.content.Context
import androidx.room.Room
import com.example.dayplanner.data.database.entities.TaskEntity
import com.example.dayplanner.data.database.entities.TaskTimeEntity
import com.example.dayplanner.data.database.entities.testTaskEntities
import com.example.dayplanner.data.database.entities.testTaskTimeEntities
import java.util.concurrent.Executor

/**
 * returns a prepopulated [TaskDao]
 * @param context the [Context] for the [TaskDatabase]
 * @param taskEntities the list of [TaskEntity] to be saved
 * @param taskTimeEntities the list of [TaskTimeEntity] to be saved
 */
suspend fun getPrePopulatedDatabase(
    context: Context,
    taskEntities: List<TaskEntity> = testTaskEntities,
    taskTimeEntities: List<TaskTimeEntity> = testTaskTimeEntities,
    executor: Executor? = null

): TaskDao {
    val database = Room.inMemoryDatabaseBuilder(
        context, TaskDatabase::class.java).apply { if (executor != null ) setQueryExecutor(executor).setTransactionExecutor(executor) }.build()
    val dao = database.dao()

    for (task in taskEntities) {
        dao.saveTaskEntity(task)
    }

    for (taskTime in taskTimeEntities) {
        dao.saveTaskTimeEntity(taskTime)
    }
    return dao
}