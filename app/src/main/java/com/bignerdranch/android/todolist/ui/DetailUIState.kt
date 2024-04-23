package com.bignerdranch.android.todolist.ui

import com.bignerdranch.android.todolist.data.database.Todo
import com.bignerdranch.android.todolist.data.database.TodoPriority
import java.util.Date
import java.util.UUID

data class DetailUIState(
    val databaseTodo: Todo,
    val showDatePicker: Boolean = false,
    val showPriorityDropDown: Boolean = false,
    val showDueDate: Boolean = false
)