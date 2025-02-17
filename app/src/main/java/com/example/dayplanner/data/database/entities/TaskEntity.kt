package com.example.dayplanner.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * entity in the database that represents a task
 * @param name the name of the Task
 */
@Entity
data class TaskEntity(@PrimaryKey(autoGenerate = false) val name: String) {

    companion object {
        /**
         * checks whether [name] is a valid name for a [TaskEntity]
         */
        fun isValidTaskName(name: String) = name.isNotBlank()
    }

    init {
        require(isValidTaskName(name)) { "name: $name is not valid for a Task" }
    }
}
