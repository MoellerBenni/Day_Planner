package com.example.dayplanner.data.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dayplanner.data.database.entities.TaskEntity
import com.example.dayplanner.data.database.entities.TaskTimeEntity

/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [TaskEntity::class, TaskTimeEntity::class], version = 1, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun dao(): TaskDao

    companion object {
        @Volatile
        private var Instance: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TaskDatabase::class.java, "task_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}