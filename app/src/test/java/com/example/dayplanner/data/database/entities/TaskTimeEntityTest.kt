package com.example.dayplanner.data.database.entities

import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.test.assertFailsWith


class TaskTimeEntityTest {

    private val startTime = LocalTime.of(10, 0)
    private val endTime = LocalTime.of(11, 0)

    @Test
    fun createTaskTime_Valid() {
        TaskTimeEntity(taskName = "test", weekDay = DayOfWeek.MONDAY, startTime = startTime, endTime = endTime)
    }

    @Test
    fun createTaskTime_StartTimeNotBeforeEndTime() {
        assertFailsWith<IllegalArgumentException> {
            TaskTimeEntity(taskName = "test", weekDay = DayOfWeek.MONDAY, startTime = startTime, endTime = startTime)
        }
    }
}