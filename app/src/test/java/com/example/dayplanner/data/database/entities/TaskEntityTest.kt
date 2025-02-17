package com.example.dayplanner.data.database.entities

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertFailsWith


class TaskEntityTest {

    private val testName = "test Task Name"

    @Test
    fun isValidTaskName_Valid() {
        val isValid = TaskEntity.isValidTaskName(testName)
        assertTrue(isValid)
    }

    @Test
    fun isValidTaskName_EmptyString() {
        val name = ""
        val isValid = TaskEntity.isValidTaskName(name)
        assertFalse(isValid)
    }

    @Test
    fun isValidTaskName_BlankString() {
        val name = " "
        val isValid = TaskEntity.isValidTaskName(name)
        assertFalse(isValid)
    }

    @Test
    fun createTaskEntity_Valid() {
        TaskEntity(name = testName)
    }

    @Test
    fun createTaskEntity_Invalid() {
        val invalidName = ""
        assertFailsWith<IllegalArgumentException> { TaskEntity(name = invalidName) }
    }


}