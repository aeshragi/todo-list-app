package com.bignerdranch.android.todolist.ui.screens.detail

import com.bignerdranch.android.todolist.data.database.Todo

data class DetailUIState(
    val databaseTodo: Todo,
    val showDatePicker: Boolean = false,
    val showPriorityDropDown: Boolean = false,
    val showDueDate: Boolean = false
)