package com.example.dayplanner.model

import java.time.DayOfWeek

/**
 * represents a time frame in a Week
 * @param weekDay the [DayOfWeek] of this [WeekDayTimeFrame]
 * @param timeFrame the [TimeFrame] of this [WeekDayTimeFrame]
 */
data class WeekDayTimeFrame(val weekDay: DayOfWeek, val timeFrame: TimeFrame)
