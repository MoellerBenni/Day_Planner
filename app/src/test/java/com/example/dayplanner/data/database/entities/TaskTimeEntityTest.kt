package com.example.dayplanner.data.database.entities

import com.example.dayplanner.model.TimeFrame
import com.example.dayplanner.model.WeekDayTimeFrame
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.test.assertEquals
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

    @Test
    fun toWeekDayTimeFrame() {
        val taskTimeEntity = TaskTimeEntity(taskName = "test", weekDay = DayOfWeek.MONDAY, startTime = startTime, endTime = endTime)
        val expectedWeekDayTimeFrame =
            WeekDayTimeFrame(weekDay = taskTimeEntity.weekDay, timeFrame = TimeFrame(startTime = startTime, endTime = endTime))
        assertEquals(expectedWeekDayTimeFrame, taskTimeEntity.toWeekDayTimeFrame())
    }
}