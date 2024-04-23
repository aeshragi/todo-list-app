package com.bignerdranch.android.todolist.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

enum class TodoPriority(val value: Int) {
    P0(0),
    P1(1),
    P2(2),
    P3(3),
    P4(4);
    companion object {
        fun fromInt(value: Int) = TodoPriority.values().first { it.value == value }
    }
}

@Entity
data class Todo(
    @PrimaryKey val id: UUID,
    val title: String,
    val content: String,
    val isDone: Boolean,
    val priority: TodoPriority = TodoPriority.P4,
    val dateCreated: Date? = null,
    val dateDue: Date? = null
)
