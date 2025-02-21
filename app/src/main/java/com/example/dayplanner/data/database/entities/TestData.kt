package com.example.dayplanner.data.database.entities

import java.time.DayOfWeek
import java.time.LocalTime

val testTaskEntities = listOf(TaskEntity(name = "Chores"), TaskEntity(name = "University"))

val testTaskTimeEntities =
    listOf(
        TaskTimeEntity(taskName = testTaskEntities[0].name, weekDay = DayOfWeek.MONDAY, startTime = LocalTime.of(8, 0), endTime = LocalTime.of(9, 0)),
        TaskTimeEntity(taskName = testTaskEntities[0].name, weekDay = DayOfWeek.WEDNESDAY, startTime = LocalTime.of(8, 0), endTime = LocalTime.of(9, 0)),
        TaskTimeEntity(taskName = testTaskEntities[0].name, weekDay = DayOfWeek.FRIDAY, startTime = LocalTime.of(8, 0), endTime = LocalTime.of(9, 0)),
        TaskTimeEntity(taskName = testTaskEntities[1].name, weekDay = DayOfWeek.TUESDAY, startTime = LocalTime.of(9, 0), endTime = LocalTime.of(10, 0)),
        TaskTimeEntity(taskName = testTaskEntities[1].name, weekDay = DayOfWeek.THURSDAY, startTime = LocalTime.of(9, 0), endTime = LocalTime.of(10, 0)),
        TaskTimeEntity(taskName = testTaskEntities[1].name, weekDay = DayOfWeek.SATURDAY, startTime = LocalTime.of(9, 0), endTime = LocalTime.of(10, 0))
    )
