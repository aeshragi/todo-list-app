package com.bignerdranch.android.todolist.ui

import com.bignerdranch.android.todolist.data.database.Todo

enum class TodoSort {
    PRIORITY,
    DUE_DATE,
    DATE_CREATED,
}

data class ListUiState(
    val todos: List<Todo>,
    val todoSort: TodoSort = TodoSort.DATE_CREATED,
    val showCompleted: Boolean = false,
    val sortExpanded: Boolean = false
)
