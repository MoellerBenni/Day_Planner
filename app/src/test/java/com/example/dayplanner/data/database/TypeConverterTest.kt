package com.example.dayplanner.data.database

import org.junit.Test
import java.time.DateTimeException
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class TypeConverterTest {
    private val typeConverter = TypeConverter()
    private val testTime = LocalTime.of(10, 0)

    @Test
    fun localTimeToValue() {
        val value = typeConverter.localTimeToValue(testTime)
        val expectedValue = testTime.toSecondOfDay()
        assertEquals(value, expectedValue)
    }

    @Test
    fun valueToLocalTime() {
        val value = testTime.toSecondOfDay()
        val localTime = typeConverter.valueToLocalTime(value)
        assertEquals(testTime, localTime)
    }

    @Test
    fun valueToLocalTime_InvalidValue() {
        val value = -1
        assertFailsWith<DateTimeException> { typeConverter.valueToLocalTime(value) }
    }
}