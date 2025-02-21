package com.example.dayplanner.data.database

import androidx.room.TypeConverter
import java.time.DateTimeException
import java.time.LocalTime

class TypeConverter {
    
    /**
     * transforms [localTime] to an [Int] time stamp of seconds
     */
    @TypeConverter
    fun localTimeToValue(localTime: LocalTime) = localTime.toSecondOfDay()

    /**
     * transforms [value] to a [LocalTime]
     * @throws DateTimeException when [value] is invalid
     */
    @TypeConverter
    fun valueToLocalTime(value: Int): LocalTime = LocalTime.ofSecondOfDay(value.toLong())
}
