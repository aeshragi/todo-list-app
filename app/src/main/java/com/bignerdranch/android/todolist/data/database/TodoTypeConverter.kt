package com.bignerdranch.android.todolist.data.database

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

class TodoTypeConverter {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long): Date {
        return Date(millisSinceEpoch)
    }

    @TypeConverters
    fun fromPriority(priority: TodoPriority): Int {
        return priority.value
    }

    @TypeConverters
    fun toPriority(ordinalVal: Int): TodoPriority {
        return TodoPriority.fromInt(ordinalVal)
    }
}