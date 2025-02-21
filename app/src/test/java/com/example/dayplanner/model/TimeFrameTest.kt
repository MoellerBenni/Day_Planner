package com.example.dayplanner.model

import org.junit.Test
import java.time.LocalTime
import kotlin.test.assertFailsWith


class TimeFrameTest {
    private val startTime = LocalTime.of(10, 0)
    private val endTime = LocalTime.of(11, 0)

    @Test
    fun createTimeFrame() {
        TimeFrame(startTime = startTime, endTime = endTime)
    }

    @Test
    fun createTimeFrame_StartTimeNotBeforeEndTime() {
        assertFailsWith<IllegalArgumentException> { TimeFrame(startTime = startTime, endTime = startTime) }
    }
}