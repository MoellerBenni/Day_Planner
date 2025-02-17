package com.example.dayplanner.model

import java.time.LocalTime

/**
 * data class the represents a time frame with [startTime] and [endTime]
 * @throws IllegalArgumentException when [startTime] is not before [endTime]
 */
data class TimeFrame(val startTime: LocalTime, val endTime: LocalTime) {

    init {
        require(startTime.isBefore(endTime)) { "Start Time $startTime was not before End Time $endTime" }
    }
}
