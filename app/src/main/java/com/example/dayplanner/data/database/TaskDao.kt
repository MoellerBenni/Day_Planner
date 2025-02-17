package com.example.dayplanner.data.database

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import com.example.dayplanner.data.database.entities.TaskEntity
import com.example.dayplanner.data.database.entities.TaskTimeEntity

/**
 * Dao for the [TaskDatabase]
 */
@Dao
interface TaskDao {

    /**
     * saves [taskEntity] to the database
     * @throws SQLiteConstraintException when there is already another [TaskEntity] with the same [PrimaryKey]
     */
    @Insert
    suspend fun saveTaskEntity(taskEntity: TaskEntity)

    /**
     * deletes [TaskEntity] from the database
     */
    @Delete
    suspend fun deleteTaskEntity(taskEntity: TaskEntity)

    /**
     * retrieves a [TaskEntity] with [name] from the database
     */
    @Query("SELECT * FROM task WHERE name = :name")
    suspend fun getTaskEntityByName(name: String): TaskEntity?


    /**
     * saves [taskTimeEntity] to the database
     * @throws SQLiteConstraintException when there is already another [TaskTimeEntity] with the same [PrimaryKey]
     */
    @Insert
    suspend fun saveTaskTimeEntity(taskTimeEntity: TaskTimeEntity)

    /**
     * retrieves all [TaskTimeEntity] of a [TaskEntity] with [taskName]
     */
    @Query("SELECT * FROM task_time WHERE task_name = :taskName")
    suspend fun getTaskTimesOfTaskEntity(taskName: String): List<TaskTimeEntity>

}