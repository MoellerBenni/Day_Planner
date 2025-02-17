package com.example.dayplanner.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * entity in the database that represents a [TaskEntity] being planed at specific time
 * @param taskName the name of the [TaskEntity] this [TaskTimeEntity] belongs to
 * @param weekDay the [DayOfWeek] this [TaskTimeEntity] is planned on
 * @param startTime the [LocalTime] this [TaskTimeEntity] starts on
 * @param endTime the [LocalTime] this [TaskTimeEntity] ends on
 */
@Entity(
    tableName = "task_time",
    foreignKeys = [ForeignKey(
        entity = TaskEntity::class,
        parentColumns = ["name"],
        childColumns = ["task_name"],
        onDelete = ForeignKey.CASCADE
    )], primaryKeys = ["task_name", "week_day", "start_time", "end_time"]
)
data class TaskTimeEntity(
    @ColumnInfo(name = "task_name") val taskName: String,
    @ColumnInfo(name = "week_day") val weekDay: DayOfWeek,
    @ColumnInfo(name = "start_time") val startTime: LocalTime,
    @ColumnInfo(name = "end_time") val endTime: LocalTime,
) {
    init {
        require(startTime.isBefore(endTime)) { "Start Time $startTime was not before End Time $endTime" }
    }
}
